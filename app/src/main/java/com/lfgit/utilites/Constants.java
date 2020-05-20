package com.lfgit.utilites;
import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Constants {
    public static final String PKG = "com.lfgit/";
    public static final String APP_DIR = "/data/data" + "/" + PKG;
    public static final String FILES_DIR = APP_DIR + "files";
    public static final String USR_DIR = FILES_DIR + "/usr";
    public static final String USR_STAGING_DIR = FILES_DIR + "/usr-staging";
    public static final String LIB_DIR = USR_DIR + "/lib";
    public static final String BIN_DIR = USR_DIR + "/bin";
    public static final String REPOS_DIR = APP_DIR + "/repos";
    public static final String GIT_CORE_DIR = FILES_DIR + "/libexec/git-core";
    public static final String HOOKS_DIR = FILES_DIR + "/hooks";
    public static String EXT_STORAGE = Environment.getExternalStorageDirectory().toString() + "/";


    /** Pending Git Task */
    public enum PendingTask {
        CLONE,
        SHALLOW_CLONE,
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
        LFS_TRACK,
        LFS_UNTRACK,
        LFS_LIST_PATTERNS,
        LFS_LIST_FILES,
        LFS_PRUNE,
        LFS_STATUS,
        LFS_ENV,
        CONFIG,
        NONE,
    }

    /** Inner state of a task */
    public enum InnerState {
        IS_REPO,
        FOR_APP,
        FOR_USER,
        GET_REMOTE_GIT,
        ADD_ORIGIN_REMOTE,
        SET_ORIGIN_REMOTE,
    }

    /** Check if path is writable. */
    public static Boolean isWritablePath(String path) {
        File f = new File(path);
        return f.canWrite();
    }

    /** Make directories if they don't exist yet.
     *  Returns path as a File.
     * */
    public static File mkdirsIfNotExist(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /** Returns the Git directory name ( = the project name) */
    public static String getGitDir(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String lastPathSegment = path.substring(path.lastIndexOf("/") + 1);

        int index = lastPathSegment.lastIndexOf(".git");
        if (index > 0) {
            lastPathSegment = lastPathSegment.substring(0, index);
        }
        return lastPathSegment;
    }
}
