package com.lfgit.tasks;

import android.app.Activity;

import static com.lfgit.Constants.binDir;
import static com.lfgit.Constants.filesDir;
import static com.lfgit.Constants.usrDir;

public class GitAnnexExec extends Executor {

    public GitAnnexExec( Activity activity) {
        super(activity);
        exe = usrDir;
    }



    public String annex() {
        //envExeForRes("git-annex", "", "init");
        envExeForRes("exe/git-annex", "clone", "--library-path", usrDir + "lib/aarch64-linux-gnu:"+ usrDir + "lib:" + usrDir + "etc/ld.so.conf.d:" + usrDir + "usr/lib/aarch64-linux-gnu/audit:" + usrDir + "usr/lib/aarch64-linux-gnu/gconv", usrDir + "shimmed/git-annex/git-annex", "init");
        return getResult();
    }

}
