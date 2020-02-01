package com.lfgit.tasks;

import android.app.Activity;

import static com.lfgit.Constants.binDir;
import static com.lfgit.Constants.usrDir;

public class GitAnnexExec extends Executor {

    public GitAnnexExec( Activity activity) {
        super(activity);
        exe = usrDir;
    }



    public String annex() {
        //envExeForRes("git-annex", "", "init");
        envExeForRes("shimmed/git-annex/git-annex", "", "init");
        return getResult();
    }

}
