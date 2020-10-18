package com.example.syncer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ammarptn.gdriverest.DriveServiceHelper;
import com.ammarptn.gdriverest.GoogleDriveFileHolder;
import com.example.syncer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.UUID;

public class HomeFragment extends Fragment {

  DriveServiceHelper mDriveServiceHelper;
  GoogleAccountCredential credential;
  private String TAG = "HomeFragment";
  private HomeViewModel homeViewModel;
  private Button create_text_file;
  private Button create_folder;
  private Button search_file;
  private Button search_folder;
  private Button upload_file;
  private Button download_file;
  private Button delete_file;
  private Button view_folder;
  private DriveFolder folderName;
  private File driveFile;
  private DriveFolder driveFolder;

  public View onCreateView(@NonNull LayoutInflater inflater,
    ViewGroup container, Bundle savedInstanceState) {

    homeViewModel =
      new ViewModelProvider(this).get(HomeViewModel.class);
    View root = inflater.inflate(R.layout.fragment_home, container, false);

    // the file name will be obtained from using getText on the editText shown in the dialog
    EditText uploadFileEditText;
    create_text_file = root.findViewById(R.id.create_text_file);
    create_folder = root.findViewById(R.id.create_folder);
    search_file = root.findViewById(R.id.search_file);
    search_folder = root.findViewById(R.id.search_folder);
    upload_file = root.findViewById(R.id.upload_file);
    download_file = root.findViewById(R.id.download_file);
    delete_file = root.findViewById(R.id.delete_file_folder);
    view_folder = root.findViewById(R.id.view_file_folder);

    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
    if (account != null) {

      Log.i("homeffragment", "not null");
    }
    credential = GoogleAccountCredential.usingOAuth2(getContext(),
      Collections.singleton(DriveScopes.DRIVE_FILE));
    credential.setSelectedAccount(account.getAccount());

    Drive googledrive = new Drive.Builder(
      AndroidHttp.newCompatibleTransport(),
      new GsonFactory(),
      credential)
      .setApplicationName("Syncer")
      .build();
    mDriveServiceHelper = new DriveServiceHelper(
      DriveServiceHelper.getGoogleDriveService(getContext(), account, "Syncer"));

    create_folder.setOnClickListener(
      view -> mDriveServiceHelper.createFolder(driveFile.getName(), driveFile.getId())
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d("homefrag", "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(e -> Log.d("homefrag", "onFailure: " + e.getMessage())));

    create_text_file.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        Log.i("homefrag", "null tha");
        return;
      }
      // you can provide  folder id in case you want to save this file inside some folder.
      // if folder id is null, it will save file to the root
      mDriveServiceHelper.createTextFile("textFileName.txt", "some text", null)
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d("homefrag", "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d("homefrag", "onFailure: " + e.getMessage());
          }
        });
    });

    search_file.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      mDriveServiceHelper.searchFile(driveFile.getName(), driveFile.getMimeType())
        .addOnSuccessListener(googleDriveFileHolders -> {
          Gson gson = new Gson();
          Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolders));
        })
        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    });

    search_folder.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      mDriveServiceHelper.searchFolder("driveFolder")
        .addOnSuccessListener(googleDriveFileHolders -> {
          Gson gson = new Gson();
          Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolders));
        })
        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    });

    create_text_file.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      // you can provide  folder id in case you want to save this file inside some folder.
      // if folder id is null, it will save file to the root
      /** Remove first parameter from strings as EditText functionality is implemented */
      mDriveServiceHelper.createTextFile("uploadFileEditText.getText()", "some text",
        null) // this'll throw npe until field is initialized from editText
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d(TAG, "onFailure: " + e.getMessage());
          }
        });
    });

    create_folder.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      // you can provide  folder id in case you want to save this file inside some folder.
      // if folder id is null, it will save file to the root
      mDriveServiceHelper.createFolder(driveFolder.getDriveId().toString(), String.valueOf(UUID.randomUUID()))
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    });

    upload_file.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      mDriveServiceHelper.uploadFile(new java.io.File(getContext().getFilesDir(), "dummy.txt"),
        "text/plain", null)
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    });

    download_file.setOnClickListener(view -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      mDriveServiceHelper.downloadFile(new java.io.File(getContext().getFilesDir(), "filename.txt"),
        "google_drive_file_id_here")
        .addOnSuccessListener(aVoid -> {

        })
        .addOnFailureListener(
          e -> Log.d(TAG, "onFailure: Caught and exception while downloading:" + e));
    });

    return root;
  }

}
