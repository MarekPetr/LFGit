package com.lfgit.utilites;


import com.lfgit.activities.BasicAbstractActivity;

public class BasicFunctions {

    private static BasicAbstractActivity mActiveActivity;

    public static BasicAbstractActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(BasicAbstractActivity activity) {
        mActiveActivity = activity;
    }
}
