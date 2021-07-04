package com.happyselling.niznet.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.happyselling.niznet.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ImageScopeActivity extends AppCompatActivity {

    private ImageView imgClose, imgScope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scope);

        initComponent();
    }

    private void initComponent() {
        setupWindowAnimation(this);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Intent i = getIntent();
        String poto = i.getStringExtra("poto");

        imgClose = findViewById(R.id.img_close_scope);
        imgScope = findViewById(R.id.img_scope_image);

        loadPhoto(poto, imgScope);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTask();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishTask();
    }

    private void finishTask() {
            finish();
            runFadeInAnimation();
    }

    private void runFadeInAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fadein);
        a.reset();
        LinearLayout ll = (LinearLayout) findViewById(R.id.rl_scope_image);
        ll.clearAnimation();
        ll.startAnimation(a);
    }

    private void loadPhoto(String url, ImageView iv) {
        Picasso.get().load(url)
                .into(iv);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimation(ImageScopeActivity detailProfileActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = detailProfileActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent));
        }
    }
}
