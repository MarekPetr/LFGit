package com.lfgit.tasks;

import android.app.Activity;

import static com.lfgit.Constants.binDir;

public class GitAnnexExec extends Executor {

    public GitAnnexExec( Activity activity) {
        super(activity);
        exe = binDir;
    }



    public String annex() {
        envExeForRes("git-annex", "");
        return getResult();
    }

}
