package com.company.zeeshan.wallpaperstories.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
