package com.lfgit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.lfgit.importer.AssetImporter;
import com.lfgit.interfaces.TaskListener;
import com.lfgit.tasks.GitAnnexExec;
import com.lfgit.tasks.GitExec;
import com.lfgit.tasks.GitLfsExec;

import static com.lfgit.Logger.LogMsg;


public class MainActivity extends AppCompatActivity implements TaskListener {

    String TAG = "petr";
    ProgressDialog progressDialog;
    TextView tv1;
    GitAnnexExec annexExec;
    GitExec gitExec;
    GitLfsExec lfsExec;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.MiddleText);

        boolean install = true;

        if (isFirstRun() && install) {
            AssetImporter importer = new AssetImporter(getAssets(), this);
            importer.execute(true);
        }

        initGit();

        final Button button = findViewById(R.id.action_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                annex();
            }
        });
    }

    private void initGit() {
        annexExec = new GitAnnexExec(MainActivity.this);
        gitExec = new GitExec(MainActivity.this);
        lfsExec = new GitLfsExec(MainActivity.this);
    }

    private void ldd() {
        tv1.setText(gitExec.ldd());
    }

    private void uname() {
        tv1.setText(gitExec.uname());
    }

    private void annex() {
        tv1.setText(annexExec.annex());
    }

    private void busybox_echo() {
        tv1.setText(gitExec.busybox_echo());
    }

    private void proot() {
        tv1.setText(gitExec.proot());
    }

    private void init() {
        tv1.setText(gitExec.init("new"));
    }

    private void LFSExec() {
        tv1.setText(lfsExec.install("new"));
    }

    private Boolean isFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return false;
        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
        } else if (currentVersionCode > savedVersionCode) {
            // This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return true;
    }

    public  Boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                LogMsg("Permission is granted1");
                return true;
            } else {
                LogMsg("Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            LogMsg("Permission to read is granted1");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                LogMsg("External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    LogMsg("Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }
                break;

            case 3:
                LogMsg("External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    LogMsg("Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }
                break;
        }
    }

    @Override
    public void onTaskStarted() {
        lockScreenOrientation();
        progressDialog = ProgressDialog.show(this, "Installing...", "Getting things ready..");
    }

    @Override
    public void onTaskFinished() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            unlockScreenOrientation();
        }
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}

