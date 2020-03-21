package com.lfgit.executors;

public class GitLfsExec extends AbstractExecutor {

    GitLfsExec(ExecCallback callback) {
        super(callback);
    }

    public String install(String dest) {
        executeBinary("git-lfs",dest, "install");
        return getResult();
    }
}
