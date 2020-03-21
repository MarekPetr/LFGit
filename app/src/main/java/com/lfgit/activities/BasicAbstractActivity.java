package com.lfgit.activities;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lfgit.BuildConfig;

import org.jetbrains.annotations.NotNull;

public abstract class BasicAbstractActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST = 1;
    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showToastMsg("Write permission not granted");
                finishAffinity();
            }
        }
    }

    protected void checkAndRequestPermissions(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it from user
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST);
        }
    }

    public void showToastMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BasicAbstractActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showOptionsDialog(int title, int optionsResource, final onOptionClicked[] option_listeners) {
        CharSequence[] options_values = getResources().getStringArray(optionsResource);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(options_values, (dialog, which) ->
                option_listeners[which].onClicked()).create().show();
    }

    public interface onOptionClicked {
        void onClicked();
    }

    void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(this, "Executing", "Please wait a moment...");
    }

    void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    class InstallPreference {
        private final String PREFS_NAME = "installPref";
        private final String PREF_VERSION_CODE_KEY = "version_code";
        private int currentVersionCode = BuildConfig.VERSION_CODE;

        Boolean assetsInstalled() {
            final int DOESNT_EXIST = -1;

            // Get saved version code
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

            // Check for first run or upgrade
            if (currentVersionCode == savedVersionCode) {
                // This is just a normal run
                return true;
            } else if (savedVersionCode == DOESNT_EXIST) {
                // This is a new install (or the user cleared the shared preferences)
            } else if (currentVersionCode > savedVersionCode) {
                // This is an upgrade
            }
            return false;
        }

        void updateInstallPreference() {
            // Update the shared preferences with the current version code
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }
    }
}
