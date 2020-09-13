package com.lfgit.utilites;

import android.util.Log;

import com.lfgit.BuildConfig;

public class Logger {
    public static void LogDebugMsg(String msg) {
        String tag = "LFGit_Debug";
        if (BuildConfig.DEBUG) {
            if (msg == null) msg = "null";
            else if (msg.length() == 0) {
                msg = "EMPTY MSG";
            }
            Log.d(tag, msg);
        }
    }
}
