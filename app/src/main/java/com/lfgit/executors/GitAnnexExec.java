package com.lfgit.executors;


import static com.lfgit.utilites.Constants.USR_DIR;

public class GitAnnexExec extends AbstractExecutor {

    GitAnnexExec(ExecCallback callback) {
        super(callback);
        super.mExeDir = USR_DIR;
    }
}
