package com.company.zeeshan.wallpaperstories.Activities;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class DetailActivity extends AppCompatActivity {

    Bitmap bitmap = null;
    InterstitialAd mInterstitialAd;

    ByteArrayInputStream inputStream;

    @Override
    @SuppressLint("StaticFieldLeak")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {


            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            final AdView mAdView = findViewById(R.id.adView);

            final Bundle bundle = getIntent().getExtras();
            getSupportActionBar().setTitle("");

            final String image = bundle.getString(UniversalConstants.IMAGE_DETAILS_URL);
            final WeakReference<String> imageRef = new WeakReference<String>(image);
            final ProgressBar progressBar = findViewById(R.id.detailLoading);
            final ImageView imageView = findViewById(R.id.imageDetail);


            new AsyncTask<Void, AdRequest, AdRequest>() {
                @Override
                protected AdRequest doInBackground(Void... voids) {

                    return new AdRequest.Builder().build();
                }

                @Override
                protected void onPostExecute(AdRequest adRequest) {
                    super.onPostExecute(adRequest);
                    mAdView.loadAd(adRequest);

                }
            }.execute();

            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Bitmap doInBackground(Void... voids) {


                    try {
                        bitmap = Picasso.with(getApplicationContext()).load(imageRef.get()).get();
                        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 10, arrayOutputStream);
                            byte[] bitmapData = arrayOutputStream.toByteArray();
                            inputStream = new ByteArrayInputStream(bitmapData);
                        }
                    } catch (IOException io) {
                        Toast.makeText(getApplicationContext(), io.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }.execute();

            final FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    wallpaperManager.setStream(inputStream, null, true, WallpaperManager.FLAG_SYSTEM);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            Snackbar.make(view, "Wallpaper Set", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }.execute();


                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_image_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();

        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && inputStream != null) {
            bitmap.recycle();
            inputStream.reset();
        }
    }
}
