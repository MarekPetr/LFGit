package com.lfgit.tasks;

import android.app.Activity;

import static com.lfgit.Constants.*;
import static com.lfgit.Constants.reposDir;

public class GitLfsExec extends Executor {
    public GitLfsExec(Activity activity) {
        super(activity);
    }

    public String install(String dest) {
        envExeForRes("git-lfs",dest, "install");
        return getResult();
    }
}
