package com.lfgit.tasks;

public class GitLfsExec extends AbstractExecutor {

    public String install(String dest) {
        envExeForRes("git-lfs",dest, "install");
        return getResult();
    }
}
