package com.lfgit.tasks;

import static com.lfgit.utilites.Constants.REPOS_DIR;
import static com.lfgit.utilites.Logger.LogMsg;

public class GitExec extends AbstractExecutor {

    private String gitPath = "git";

    public void config() {
        executeBinary(gitPath, "","config", "--global", "user.email", "petr.marek18@gmail.com");
        LogMsg(getResult());
        executeBinary(gitPath, "", "config", "--global", "user.name", "MarekPetr");
        LogMsg(getResult());
    }

    public String busybox_echo() {
        executeBinary("busybox", "", "echo", "ahoj");
        return getResult();
    }

    public String init(String dest) {
        String gitOperation = "init";
        return executeBinary(gitPath, dest, gitOperation);
    }

    public String commit(String dest) {
        String gitOperation = "commit";
        String message = "-m\"newFileToCommit\"";
        executeBinary(gitPath, dest, gitOperation, message);
        return getResult();
    }

    public String clone(String dest, String userName, String password) {
        String gitOperation = "clone";
        String url = "https://" + userName + ":" + password + "@github.com/MarekPetr/test";
        executeBinary(gitPath, dest, gitOperation, url);
        return getResult();
    }

    public String status(String dest) {
        String gitOperation = "status";
        executeBinary(gitPath, dest, gitOperation);
        return getResult();
    }

    public String addAllToStage(String dest) {
        String gitOperation = "add";
        return executeBinary(gitPath, dest, gitOperation, ".");
    }

    public String push(String dest) {
        String gitOperation = "push";
         executeBinary(gitPath, dest, gitOperation);
        return getResult();
    }




}
