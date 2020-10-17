package com.example.syncer;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

public class testactivity extends AppCompatActivity {

    TextView name;
    String personName;
    GoogleAccountCredential credential;
    DriveServiceHelper mdriveservicehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testactivity);

        name = findViewById(R.id.name);

         credential  = GoogleAccountCredential.usingOAuth2(getBaseContext(), Collections.singleton(DriveScopes.DRIVE_FILE));

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Drive googledrive = new Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new GsonFactory(),
                    credential)
                    .setApplicationName("Syncer")
                    .build();

            mdriveservicehelper = new DriveServiceHelper(googledrive);

        }

        if(personName!=null)
        Log.i("testactivity",personName.toString());
        else
            Log.i("test","null");

        name.setText(personName);
    }
}