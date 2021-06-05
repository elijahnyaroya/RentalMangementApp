package com.elisuntech.rentalmanagementapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.elisuntech.rentalmanagementapp2.Auths.login;
import com.elisuntech.rentalmanagementapp2.MainContent.dashboard;
import com.elisuntech.rentalmanagementapp2.commonMethods.sharedPreference;

public class SplashScrActivity extends AppCompatActivity {
    private ImageView logoSplash;
    private Animation anim1, anim2, anim3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_scr);
        sharedPreference mysessions = new sharedPreference(SplashScrActivity.this);

        if (mysessions.getIsLoggedIn().equals("0")){
            Intent intent = new Intent(SplashScrActivity.this, login.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(SplashScrActivity.this, dashboard.class);
            startActivity(intent);
        }
        init();


        logoSplash.startAnimation(anim1);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoSplash.startAnimation(anim2);
                logoSplash.setVisibility(View.GONE);
                finish();
                startActivity(new Intent(SplashScrActivity.this, login.class));


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void init(){

        logoSplash = findViewById(R.id.ivLogoSplash);
        anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout);
        anim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein);
    }


}
