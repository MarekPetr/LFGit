package com.lfgit.executors;

public class GitLfsExec extends AbstractExecutor {

    public String install(String dest) {
        executeBinary("git-lfs",dest, "install");
        return getResult();
    }
}
