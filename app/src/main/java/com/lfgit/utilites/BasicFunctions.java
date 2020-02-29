package com.lfgit.utilites;


import android.os.Environment;

import com.lfgit.activities.BasicAbstractActivity;

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
