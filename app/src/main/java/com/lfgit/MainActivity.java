package com.lfgit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.lfgit.importer.AssetImporter;
import com.lfgit.tasks.GitExec;
import com.lfgit.tasks.GitLfsExec;

import static com.lfgit.permissions.PermissionRequester.isTermuxExePermissionGranted;


public class MainActivity extends AppCompatActivity implements TaskListener{

    String TAG = "petr";
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isFirstRun()) {
            AssetImporter importer = new AssetImporter(getAssets(), this);
            importer.execute(true);
        }

        final Button button = findViewById(R.id.action_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exeTermux();
            }
        });
    }

    private void exeTermux() {
        if (isTermuxExePermissionGranted(MainActivity.this)) {
            Uri myUri = Uri.parse("com.termux.file:/data/data/com.termux/files/home/git-annex.linux/git-annex" );
            Intent executeIntent = new Intent("com.termux.service_execute", myUri);
            executeIntent.setClassName("com.termux", "com.termux.app.TermuxService");

            // Whether to execute script in background.
            //executeIntent.putExtra("com.termux.execute.background", true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(executeIntent);
            } else {
                getApplicationContext().startService(executeIntent);
            }
        }
    }

    private void busybox_echo() {
        String res = "";
        GitExec gitExec = new GitExec(MainActivity.this);
        res = gitExec.busybox_echo();

        TextView tv1 = findViewById(R.id.MiddleText);
        tv1.setText(res);}


    private void init() {
        String res = "";
        GitExec gitExec = new GitExec(MainActivity.this);
        res = gitExec.init("new");

        TextView tv1 = findViewById(R.id.MiddleText);
        tv1.setText(res);
    }

    private void LFSExec() {
        String res = "";
        GitLfsExec GitLfsExec = new GitLfsExec(MainActivity.this);
        res = GitLfsExec.install("new");

        TextView tv1 = findViewById(R.id.MiddleText);
        tv1.setText(res);
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



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission

                }
                break;

            case 2:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                }
                break;

            case 3:
                Log.d(TAG, "Exe permission");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
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

