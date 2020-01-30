package com.lfgit.tasks;

import android.app.Activity;

import static com.lfgit.Constants.*;
import static com.lfgit.Constants.reposDir;

public class GitLfsExec extends Executor {
    public GitLfsExec(Activity activity) {
        super(activity);
    }

    private String install() {
        envExeForRes("git-lfs",reposDir, "install");
        return getResult();
    }
}
