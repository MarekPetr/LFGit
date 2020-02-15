package com.lfgit.tasks;

import android.app.Activity;

public class GitLfsExec extends Executor {

    public String install(String dest) {
        envExeForRes("git-lfs",dest, "install");
        return getResult();
    }
}
