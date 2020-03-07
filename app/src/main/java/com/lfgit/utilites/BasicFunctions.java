package com.lfgit.utilites;
import android.os.Environment;
import com.lfgit.activities.BasicAbstractActivity;

public class BasicFunctions {

    // TODO preferences
    public static String getReposPath() {
        return Environment.getExternalStorageDirectory().toString() + "/LfGit/";
    }
}
