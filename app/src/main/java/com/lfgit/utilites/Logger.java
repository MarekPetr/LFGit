package com.lfgit.utilites;

import android.util.Log;

import com.lfgit.BuildConfig;

public class Logger {
    static public void LogMsg(String msg) {
        if (BuildConfig.DEBUG) {
            if (msg == null) msg = "null";
            else if (msg.length() == 0) {
                msg = "EMPTY MSG";
            }
            Log.d("mylogger", msg);
        }
    }
}
