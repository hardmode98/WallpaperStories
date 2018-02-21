package com.company.zeeshan.wallpaperstories.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.company.zeeshan.wallpaperstories.R;

public class ImageDetails extends AppCompatActivity {

    @SuppressLint({"ClickableViewAccessibility", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);




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
