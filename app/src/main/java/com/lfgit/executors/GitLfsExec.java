package com.lfgit.executors;

import com.lfgit.utilites.TaskState;

public class GitLfsExec extends AbstractExecutor {

    GitLfsExec(ExecListener callback) {
        super(callback);
    }

    public void install(String dest, TaskState state) {
        executeBinary("git-lfs",dest, "install");
    }
}
