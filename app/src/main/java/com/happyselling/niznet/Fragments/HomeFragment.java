package com.happyselling.niznet.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.CartListActivity;
import com.happyselling.niznet.Activities.ProductPage.ProductListActivity;
import com.happyselling.niznet.Adapters.BrandsAdapter;
import com.happyselling.niznet.Adapters.CategoryAdapter;
import com.happyselling.niznet.Adapters.NewProductAdapter;
import com.happyselling.niznet.LoginActivity;
import com.happyselling.niznet.Models.BrandsResponse.Brands;
import com.happyselling.niznet.Models.CategoryResponse.Category;
import com.happyselling.niznet.Models.NewProductResponse.Datum;
import com.happyselling.niznet.Models.NewProductResponse.Product;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment  extends Fragment {
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private BaseApiService mApiService;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private ImageButton ibChart;
    private CardView cvChart;

    private FloatingSearchView searchView;

    private BrandsAdapter brandsAdapter;
    private List<com.happyselling.niznet.Models.BrandsResponse.Datum> brandsList;
    private RecyclerView rvBrands;

    private CategoryAdapter categoryAdapter;
    private List<com.happyselling.niznet.Models.CategoryResponse.Datum> categoryList;
    private RecyclerView rvCategory;

    private NewProductAdapter newProductAdapter;
    private List<Datum> newProductList;
    private RecyclerView rvNewProduct;

    private NestedScrollView scrollView;

    private SwipeRefreshLayout pullRefreshLayout;

    private CarouselView carouselViewBrands;
    private CoordinatorLayout coordinatorLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private Context context;


    private View view;
    private AsyncTask<Void,Void,String> setBrands;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        this.context = getContext();
        initComponent();
        return view;
    }

    private void initComponent() {
        mApiService = ApiServices.getApiService(context);

        scrollView = view.findViewById(R.id.sv_homepage);
        toolbar = view.findViewById(R.id.toolbar);
        searchView = view.findViewById(R.id.floating_search_view_home);

        ibChart = view.findViewById(R.id.ib_chart);
        cvChart = view.findViewById(R.id.cv_chart);

        setStatusBar();
        sharedPreferences = getActivity().getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();

        //set brands adapter
        brandsList = new ArrayList<>();
        carouselViewBrands = view.findViewById(R.id.crs_brand_home);
        setBrands();
        final Handler handler = new Handler();

        //set Category adapter
        rvCategory = view.findViewById(R.id.rv_category);
        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rvCategory.setLayoutManager(layoutManagerCategory);
        rvCategory.setItemAnimator(new DefaultItemAnimator());
        categoryList = new ArrayList<>();
        setCategory();

        rvNewProduct = view.findViewById(R.id.rv_new_product);
        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(getActivity(),2);
        rvNewProduct.setLayoutManager(layoutManager1);
        rvNewProduct.setItemAnimator(new DefaultItemAnimator());
        newProductList = new ArrayList<>();
        setNewProduct();

        coordinatorLayout = view.findViewById(R.id.cl_home_page);
        pullRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_home);
        appBarLayout = view.findViewById(R.id.appbar);
        pullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                   @Override
                                                   public void onRefresh() {
                                                       initComponent();

                                                       handler.postDelayed(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               pullRefreshLayout.setRefreshing(false);
                                                           }
                                                       }, 500);
                                                   }
                                               });
                ibChart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (status_login.equals("1")) {
                            startActivity(new Intent(getActivity(), CartListActivity.class));
                        } else {
                            Toast.makeText(getActivity(), "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                        }
                    }
                });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Intent i = new Intent(getActivity(), ProductListActivity.class);
                i.putExtra("search_param", currentQuery);
                getActivity().startActivity(i);
            }
        });
    }

    private void setNewProduct() {
        mApiService.getNewProduct().enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful()){
                    if(response.body().getMeta().getCode() == 200){
                        newProductAdapter = new NewProductAdapter(view.getContext(),response.body().getData());
                        newProductAdapter.notifyDataSetChanged();
                        rvNewProduct.setAdapter(newProductAdapter);
                    }else{
                        Toast.makeText(view.getContext(), "Oops, you are disconnect", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Oops, you are disconnect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCategory() {
        mApiService.getAllCategory().enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if(response.isSuccessful()){
                    if(response.body().getMeta().getCode() == 200){
                        categoryAdapter = new CategoryAdapter(view.getContext(),response.body().getData());
                        categoryAdapter.notifyDataSetChanged();
                        rvCategory.setAdapter(categoryAdapter);
                    }else{
                        Toast.makeText(view.getContext(), "Oops, you are disconnect", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Oops, you are disconnect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  setBrands(){
        mApiService.getAllBrands().enqueue(new Callback<Brands>() {
            @Override
            public void onResponse(Call<Brands> call, Response<Brands> response) {
                if(response.isSuccessful()){
                    if(response.body().getMeta().getCode() == 200){
                    brandsList = response.body().getData();
                    addPhoto();

                    }else{
                        Toast.makeText(view.getContext(), "Oops, you are disconnect", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Oops, you are disconnect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Brands> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final class SetBrandsAdapter extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            setBrands();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }


    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setTransparentForImageViewInFragment(getActivity(), null);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > scrollX){
//                    appBarLayout.setVisibility(View.VISIBLE);
                    searchView.setBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                    cvChart.setCardBackgroundColor(getActivity().getResources().getColor(R.color.light_gray));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    StatusBarUtil.setColor(getActivity(), getActivity().getResources().getColor(R.color.white), 0);
                    Log.d("Masuk Sini", "onScrollChange:turun ");
                } else{
//                    appBarLayout.setVisibility(View.GONE);
                    searchView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

                    cvChart.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    StatusBarUtil.setTransparentForImageViewInFragment(getActivity(), null);
                    Log.d("Masuk Sini", "onScrollChange: naik");
                }
            }
        });
    }

    private void addPhoto() {
        carouselViewBrands.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Picasso.get().load(brandsList.get(position).getBrandIcon())
                        .into(imageView);

            }
        });
        carouselViewBrands.setPageCount(brandsList.size());
        carouselViewBrands.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Intent i = new Intent(getActivity(), ProductListActivity.class);
                i.putExtra("search_param", brandsList.get(position).getBrandName());
                getActivity().startActivity(i);
            }
        });
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if(status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
