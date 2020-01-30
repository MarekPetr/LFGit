package com.lfgit;

import android.util.Log;

public class Logger {
    static public void LogAny(String msg) {
        if (msg.length() == 0) {
            msg = "NO ANSWER";
        }
        Log.d("petr", msg);
    }
}
