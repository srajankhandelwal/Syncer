package com.example.syncer.ui.home;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ammarptn.gdriverest.DriveServiceHelper;
import com.example.syncer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveFolder;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import com.obsez.android.lib.filechooser.ChooserDialog;
import java.util.Collections;

public class HomeFragment extends Fragment {

  String TAG = "HomeFragment";
  DriveServiceHelper mDriveServiceHelper;
  GoogleAccountCredential credential;
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

  public static String getMimeType(String url) {
    String type = null;
    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
    if (extension != null) {
      type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
    return type;
  }

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

    return root;
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
    if (account != null) {

      Log.i(TAG, "not null");
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

    create_folder.setOnClickListener(create_folder -> {
      java.io.File testFile;
      new ChooserDialog(requireActivity())
        .withFilter(true, false)
        .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
        // to handle the result(s)
        .withChosenListener((path, folder) -> {
          Toast.makeText(getContext(), "FOLDER: " + path, Toast.LENGTH_SHORT).show();
          //createFolder(testFile, null);
        })
        .build()
        .show();
    });

    create_text_file.setOnClickListener(here -> {
      if (mDriveServiceHelper == null) {
        Log.i(TAG, "null tha");
        return;
      }
      // you can provide  folder id in case you want to save this file inside some folder.
      // if folder id is null, it will save file to the root
      mDriveServiceHelper.createTextFile("textfilename.txt", "some text", null)
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d("homefrag", "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(e -> Log.d("homefrag", "onFailure: " + e.getMessage()));
    });

    search_file.setOnClickListener(it -> {
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

    search_folder.setOnClickListener(vie -> {
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

    create_text_file.setOnClickListener(v -> {
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
        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    });

    download_file.setOnClickListener(vi -> {
      if (mDriveServiceHelper == null) {
        return;
      }
      mDriveServiceHelper.downloadFile(
        new java.io.File(getContext().getFilesDir(), "filename.txt"), "google_drive_file_id_here")
        .addOnSuccessListener(aVoid -> {

        })
        .addOnFailureListener(e -> {

        });
    });
  }

  void createFolder(java.io.File folder, String folderId) {
    mDriveServiceHelper.createFolder(folder.getName(), folderId)
      .addOnSuccessListener(googleDriveFileHolder -> {
        Gson gson = new Gson();
        Log.d(TAG, "onSuccess: Folder Created " + gson.toJson(googleDriveFileHolder));
        java.io.File[] files = folder.listFiles();
        if (files != null) {
          Log.d("Files", "Size: " + files.length);
          for (java.io.File file : files) {
            Log.d("Files", "FileName:" + file.getName());
            Runnable runnable = () -> uploadFile(file, googleDriveFileHolder.getId());
            new Thread(runnable).start();
          }
        }
      })
      .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
  }

  void uploadFile(java.io.File file, String folderId) {
    if (mDriveServiceHelper == null) {
      return;
    }
    if (getMimeType(file.getAbsolutePath()) != null) {
      mDriveServiceHelper.uploadFile(
        new java.io.File(file.getParentFile().getAbsolutePath(), file.getName()),
        getMimeType(file.getAbsolutePath()), folderId)
        .addOnSuccessListener(googleDriveFileHolder -> {
          Gson gson = new Gson();
          Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
        })
        .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    } else {
      createFolder(file, folderId);
    }
  }
}
