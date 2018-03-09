package com.company.zeeshan.wallpaperstories.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.company.zeeshan.wallpaperstories.Models.Post;
import com.company.zeeshan.wallpaperstories.Models.Profile;
import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {

    Bitmap bitmap;
    InterstitialAd mInterstitialAd;
    String image;
    ByteArrayInputStream inputStream;

    @Override
    @SuppressLint({"StaticFieldLeak", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        if (getIntent().getExtras() != null) {

            //INIT ALL VIEWS
            final ImageView imageView = findViewById(R.id.imageDetail);
            final TextView name = findViewById(R.id.textView19);
            final TextView email = findViewById(R.id.textView20);
            final ImageView download = findViewById(R.id.imageView12);
            download.setVisibility(View.GONE);

            // BUNDLE EXTRACTON
            final Bundle bundle = getIntent().getExtras();
            image = bundle.getString(UniversalConstants.IMAGE_DETAILS_URL);
            String uid = bundle.getString(("uid"));
            String postedby = bundle.getString(UniversalConstants.POSTED_BY);
            String postedon = bundle.getString(UniversalConstants.POSTED_ON);
            String postText = bundle.getString(UniversalConstants.POSTTEXT);

            final Post post = new Post();
            post.imageUrl = image;
            post.postedBy = postedby;
            post.postedOn = postedon;
            post.postText = postText;
            post.uid = uid;


            ImageView back = findViewById(R.id.imageView11);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Profile profile = dataSnapshot.getValue(Profile.class);

                    name.setText(profile.name);
                    email.setText(profile.email);

                    Picasso.with(DetailActivity.this).load(profile.picture).resize(100, 100)
                            .centerCrop().into((ImageView) findViewById(R.id.profilePic));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //SHOW OWNER INFO

            // LOAD AD
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            final AdView mAdView = findViewById(R.id.adView);

            final WeakReference<String> imageRef = new WeakReference<String>(image);
            final ProgressBar progressBar = findViewById(R.id.detailLoading);

            //LOAD HEAVY ASS TASKS ASYNC
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

            //HANDLE WALLPAPER SET
            findViewById(R.id.setWallpaper).setOnClickListener(new View.OnClickListener() {
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
                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Recents").push().setValue(post);

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

            //Download

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Show Downloading
                    final ProgressBar downLoadingInd = findViewById(R.id.downloading);
                    downLoadingInd.setVisibility(View.VISIBLE);
                    download.setVisibility(View.GONE);

                    if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {


                        Random random = new Random();

                        //GENERATE A FILE WITH RANDOM NAME


                        String path = Environment.getExternalStorageDirectory().toString();
                        final File file = new File(path, random.nextInt() + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                        file.mkdirs();

                        if (bitmap != null) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {

                                    OutputStream outputStream = null;

                                    try {
                                        outputStream = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        outputStream.flush();
                                        outputStream.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);

                                    downLoadingInd.setVisibility(View.GONE);
                                    download.setVisibility(View.VISIBLE);

                                    Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
                                    try {
                                        //ADD TO GALLERY
                                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }.execute();
                        }

                    } else {

                        ActivityCompat.requestPermissions(DetailActivity.this,

                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},

                                1);

                    }


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
        } else if (item.getItemId() == R.id.download) {

            if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {


                String root = Environment.getExternalStorageDirectory().toString();
                Random random = new Random();

                File file = new File(root, String.valueOf(random.nextInt()) + ".jpg");

                if (!file.exists()) {
                    file.mkdirs();
                }

                try {

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        } else {

            ActivityCompat.requestPermissions(DetailActivity.this,

                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},

                    1);

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

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Random random = new Random();

                //GENERATE A FILE WITH RANDOM NAME

                final ProgressBar downLoadingInd = findViewById(R.id.downloading);
                downLoadingInd.setVisibility(View.VISIBLE);

                String path = Environment.getExternalStorageDirectory().toString();
                final File file = new File(path, random.nextInt() + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                file.mkdirs();

                if (bitmap != null) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            OutputStream outputStream = null;

                            try {
                                outputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            downLoadingInd.setVisibility(View.GONE);

                            Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
                            try {
                                //ADD TO GALLERY
                                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }.execute();

                } else {

                    Snackbar.make(getCurrentFocus(), "Permission Needed", Snackbar.LENGTH_SHORT).show();

                }
            }
        }
    }
}
