package com.lfgit.tasks;

public class GitLfsExec extends AbstractExecutor {

    public String install(String dest) {
        executeBinary("git-lfs",dest, "install");
        return getResult();
    }
}
