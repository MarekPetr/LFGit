package com.lfgit.utilites;

import android.util.Log;

public class Logger {
    static public void LogMsg(String msg) {
        if (msg.length() == 0) {
            msg = "NO ANSWER";
        }
        Log.d("petr", msg);
    }
}
