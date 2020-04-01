package com.lfgit.utilites;
import android.os.Environment;

import org.jetbrains.annotations.NotNull;

public class Constants {
    public static final String PKG = "com.lfgit/";
    public static final String APP_DIR = "/data/data" + "/" + PKG;
    public static final String FILES_DIR = APP_DIR + "files";
    public static final String USR_DIR = FILES_DIR + "/usr";
    public static final String LIB_DIR = USR_DIR + "/lib";
    public static final String BIN_DIR = USR_DIR + "/bin";
    public static final String REPOS_DIR = APP_DIR + "/repos";
    public static final String GIT_CORE_DIR = FILES_DIR + "/libexec/git-core";
    public static final String HOOKS_DIR = FILES_DIR + "/hooks";
    public static String EXT_STORAGE = Environment.getExternalStorageDirectory().toString() + "/";

    public enum AddRepo {
        OK(0),
        ALREADY_ADDED(1);

        int value;
        AddRepo(int value) {
            this.value = value;
        }
    }

    public enum Task {
        CLONE,
        INIT,
        COMMIT,
        ADD,
        PUSH,
        PULL,
        STATUS,
        LOG,
        RESET_HARD,
        ADD_REMOTE,
        SET_REMOTE,
        LIST_BRANCHES,
        CHECKOUT_LOCAL,
        CHECKOUT_REMOTE,
        LFS_INSTALL,
        LFS_TRACK,
        LFS_UNTRACK,
        LFS_LIST_PATTERNS,
        LFS_LIST_FILES,
        LFS_STATUS,
        LFS_ENV,
        CONFIG,
        NONE,
    }

    public enum InnerState {
        FOR_APP,
        FOR_USER,
        GET_REMOTE_GIT,
        ADD_ORIGIN_REMOTE,
        SET_ORIGIN_REMOTE,
    }
}
