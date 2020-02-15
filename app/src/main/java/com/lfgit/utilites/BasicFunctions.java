package com.lfgit.utilites;


import com.lfgit.activities.BasicActivity;

public class BasicFunctions {

    private static BasicActivity mActiveActivity;

    public static BasicActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(BasicActivity activity) {
        mActiveActivity = activity;
    }
}
