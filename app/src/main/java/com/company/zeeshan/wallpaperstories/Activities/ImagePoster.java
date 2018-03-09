package com.company.zeeshan.wallpaperstories.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.zeeshan.wallpaperstories.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class ImagePoster extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY = 2;
    Bitmap bitmap;
    FloatingActionButton fab;
    ImageView preview;
    FirebaseStorage storage;
    String uri = "";
    StorageReference storageReference;
    long numberOfPosts;
    private String mCurrentPhotoPath;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_poster);

        storage = FirebaseStorage.getInstance();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Community");

        fab =  findViewById(R.id.fab);

        preview = findViewById(R.id.postImageView);

        final EditText text = findViewById(R.id.editText);

        CardView cardView = findViewById(R.id.cardView);

        findViewById(R.id.imageView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ImagePoster.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent();

                    intent.setType("image/*");

                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);

                } else {

                    ActivityCompat.requestPermissions(ImagePoster.this,

                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},

                            1);

                }

            }

        });


        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                numberOfPosts = dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap != null) {
                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                    String date = dateFormat.format(calendar.getTime());

                    final HashMap<String, Object> data = new HashMap<>();
                    data.put("postedOn", date);
                    data.put("postText", text.getText().toString());
                    data.put("postedBy", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    data.put("postid", String.valueOf(numberOfPosts));
                    data.put("certified", "no");
                    data.put("imageUrl", uri);
                    data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    databaseReference.child(String.valueOf(numberOfPosts)).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("Uploads").push().setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(ImagePoster.this, "Your Photograph has been sent for approval", Toast.LENGTH_LONG).show();


                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(ImagePoster.this, "Added to your uploads", Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select an image", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);

            } else {

                Snackbar.make(getCurrentFocus(), "Permission Needed" , Snackbar.LENGTH_SHORT).show();

            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog progressDialog = new ProgressDialog(ImagePoster.this);


        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {


                progressDialog.setMessage("Uploading Image");
                progressDialog.show();


                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        TextView progressBar = findViewById(R.id.progressBar3);
                        progressBar.setVisibility(View.GONE);


                    }

                    @Override
                    protected Void doInBackground(Void... voids) {

                        try {
                            final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, arrayOutputStream);

                            byte[] byteArray = arrayOutputStream.toByteArray();

                            Random random = new Random();

                            storageReference = storage.getReference("images/" + random.nextInt());

                            storageReference.putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    uri = task.getResult().getDownloadUrl().toString();

                                    Picasso.with(ImagePoster.this).load(uri).resize(500, 500).centerCrop().into(preview);

                                    progressDialog.dismiss();

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });



                            return null;
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

                        bitmap.recycle();
                        storageReference =null;

                    }
                }.execute();
        }
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.dev.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
