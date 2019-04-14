package com.example.mabel.picturethis;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import bolts.Capture;

//https://www.pubnub.com/blog/creating-a-secure-login-on-android-with-oauth-auth0-and-pub/

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

       // if (account != null) {
            activityIntent = new Intent(this, Camera2Activity.class);
     //   } else {
         //   activityIntent = new Intent(this, LoginActivity.class);
   //     }

        startActivity(activityIntent);
        finish();
    }
}


