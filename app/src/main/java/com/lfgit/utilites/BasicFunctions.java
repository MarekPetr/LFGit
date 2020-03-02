package com.lfgit.utilites;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.lfgit.BuildConfig;
import com.lfgit.activities.BasicAbstractActivity;

import static android.content.Context.MODE_PRIVATE;

public class BasicFunctions {

    private static BasicAbstractActivity mActiveActivity;

    public static BasicAbstractActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(BasicAbstractActivity activity) {
        mActiveActivity = activity;
    }

    // TODO preferences
    public static String getReposPath() {
        return Environment.getExternalStorageDirectory().toString() + "/LfGit/";
    }
}
