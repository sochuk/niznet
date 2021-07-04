package com.happyselling.niznet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.jaeger.library.StatusBarUtil;
import com.happyselling.niznet.Activities.ProductPage.ProductListActivity;
import com.happyselling.niznet.R;

public class SearchActivity extends AppCompatActivity {

    private FloatingSearchView searchView;

    private ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComponent();
    }

    private void initComponent() {
        setStatusBar();
        searchView = findViewById(R.id.floating_search_view_prouct);
        searchView.setFocusable(true);
        ibBack = findViewById(R.id.ib_back_search_product);
        ibBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(SearchActivity.this, ProductListActivity.class);
              i.putExtra("search_param", "null");
              startActivity(i);
              finish();
          }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Intent i = new Intent(SearchActivity.this, ProductListActivity.class);
                i.putExtra("search_param", currentQuery);
                startActivity(i);
                finish();
            }
        });
    }

    private void setStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(this, this.getResources().getColor(R.color.white), 0);
    }

}
