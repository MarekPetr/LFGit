package com.lfgit.utilites;
import android.os.Environment;

public class BasicFunctions {

    // TODO preferences
    public static String getReposPath() {
        return Environment.getExternalStorageDirectory().toString() + "/LfGit/";
    }
}
