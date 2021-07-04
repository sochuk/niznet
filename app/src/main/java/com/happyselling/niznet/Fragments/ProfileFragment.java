package com.happyselling.niznet.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.EditProfileActivity;
import com.happyselling.niznet.MainActivity;
import com.happyselling.niznet.Models.UserRealmObject;
import com.happyselling.niznet.R;
import com.happyselling.niznet.Services.ApiServices;
import com.happyselling.niznet.Services.BaseApiService;
import com.happyselling.niznet.Utils.Enum;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Context mContext;
    private View view;
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private String status_login;
    private UserRealmObject userProfile;
    private BaseApiService mApiService;

    private de.hdodenhof.circleimageview.CircleImageView ivPotoProfileDepan;

    private TextView tvName, tvEmail, tvEdit, tvAddress, tvPhone;
    private LinearLayout llLogout;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFramgent.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getContext();
        initComponent();
        return view;
    }

    private void initComponent() {
        setStatusBar();
        sharedPreferences = mContext.getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);
        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        cekLogin();
        ivPotoProfileDepan = view.findViewById(R.id.iv_poto_profile_depan);
        tvEmail = view.findViewById(R.id.tv_email_profile_depan);
        tvName = view.findViewById(R.id.tv_nama_profile_depan);
        tvEdit = view.findViewById(R.id.tv_edit_profile_depan);
        tvAddress = view.findViewById(R.id.tv_address_profile_depan);
        tvPhone = view.findViewById(R.id.tv_telp_profile_depan);
        llLogout = view.findViewById(R.id.ll_Logout);
        Picasso.get().load(userProfile.getPhoto()).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerCrop().into(ivPotoProfileDepan);
        tvName.setText(userProfile.getName());
        tvEmail.setText(userProfile.getEmail());
        tvAddress.setText(userProfile.getAddress());
        tvPhone.setText(userProfile.getTelp());
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferencesNew = mContext.getSharedPreferences(Enum.PREF_NAME, Context.MODE_PRIVATE);

                SharedPreferences.Editor editorNew = sharedPreferencesNew.edit();
                editorNew.clear();
                editorNew.commit();

                RealmResults<UserRealmObject> realmResults = realm.where(UserRealmObject.class).findAll();
                realm.beginTransaction();
                realmResults.deleteAllFromRealm();
                realm.commitTransaction();

                Intent intent = new Intent(getActivity(), MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(getActivity(), getActivity().getResources().getColor(R.color.white), 0);
    }

    private void cekLogin() {
        status_login = sharedPreferences.getString(Enum.isloggedin, "0");
        if(status_login.equals("1")) {
            userProfile = realm.where(UserRealmObject.class).findFirst();

            mApiService = ApiServices.getApiServiceWithToken(userProfile.getToken(), mContext);
        }
    }
}
