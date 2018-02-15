package com.company.zeeshan.wallpaperstories.Activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.company.zeeshan.wallpaperstories.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ImageDetails extends AppCompatActivity {

    @SuppressLint({"ClickableViewAccessibility", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        if (Build.VERSION.SDK_INT >= 21) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,

                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        finish();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();


    }

}
