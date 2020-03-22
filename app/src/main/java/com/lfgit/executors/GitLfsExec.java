package com.lfgit.executors;

public class GitLfsExec extends AbstractExecutor {

    GitLfsExec(ExecListener callback) {
        super(callback);
    }

    public void install(String dest) {
        executeBinary("git-lfs",dest, "install");
    }
}
