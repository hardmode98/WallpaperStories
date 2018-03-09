package com.company.zeeshan.wallpaperstories.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class LoginScreen extends AppCompatActivity {

    final int GOOGLESIGNIN = 1;
    GoogleSignInClient apiClient;
    SharedPreferences loginPrefs;
    Drawable drawable;
    WeakReference<Drawable> drawableWeakReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_screen);

        loginPrefs = getSharedPreferences(UniversalConstants.LOGINPREFS, MODE_PRIVATE);

        ImageView loginBack = findViewById(R.id.loginback);
        drawable = getResources().getDrawable(R.drawable.loginback);
        drawableWeakReference = new WeakReference<>(drawable);
        loginBack.setImageDrawable(drawableWeakReference.get());


        if (loginPrefs.getInt(UniversalConstants.LOGGEDIN, 99)==1){

            startActivity(new Intent(this , MainScreen.class));
            finish();

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initiate Google Sign in
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.webserverid)).requestEmail().build();

        apiClient = GoogleSignIn.getClient(this , options);


        SignInButton signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignInIntent();

            }
        });

    }

    void SignInIntent(){

        Intent intent = apiClient.getSignInIntent();
        startActivityForResult(intent, GOOGLESIGNIN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLESIGNIN){
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{

                GoogleSignInAccount account = signInAccountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                Log.d("Error", e.getLocalizedMessage(), e);

            }
        } else {
            Toast.makeText(getApplicationContext(), "Could not ", Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            SharedPreferences.Editor editor = loginPrefs.edit();
                            editor.putInt(UniversalConstants.LOGGEDIN, 1).apply();
                            finish();
                            saveValuesToFirebase();
                            startActivity(new Intent(LoginScreen.this, MainScreen.class));


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    void saveValuesToFirebase() {

        HashMap<String, String> data = new HashMap<>();
        WeakReference<FirebaseUser> user = new WeakReference<FirebaseUser>(FirebaseAuth.getInstance().getCurrentUser());
        data.put("uid", user.get().getUid());
        data.put("name", user.get().getDisplayName());
        data.put("email", user.get().getEmail());
        data.put("picture", String.valueOf(user.get().getPhotoUrl()));

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(user.get().getUid());
        db.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawable = null;
    }
}
