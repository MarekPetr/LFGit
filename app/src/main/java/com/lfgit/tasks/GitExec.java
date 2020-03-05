package com.lfgit.tasks;

public class GitExec extends AbstractExecutor {

    private String gitPath = "git";

    public void config(String email, String username) {
        executeBinary(gitPath, ".","config", "--global", "user.name", username);
        executeBinary(gitPath, ".","config", "--global", "user.email", email);
    }

    public void setEmail(String email) {
        executeBinary(gitPath, ".","config", "--global", "user.email", email);
    }

    public void setUsername(String username) {
        executeBinary(gitPath, ".","config", "--global", "user.name", username);
    }

    public String busybox_echo() {
        return executeBinary("busybox", "", "echo", "ahoj");
    }

    public String init(String dest) {
        String gitOperation = "init";
        return executeBinary(gitPath, dest, gitOperation);
    }

    public String commit(String dest) {
        String gitOperation = "commit";
        String message = "-m \"newFileToCommit\"";
        return executeBinary(gitPath, dest, gitOperation, message);
    }

    public String clone(String dest, String userName, String password) {
        String gitOperation = "clone";
        String url = "https://" + userName + ":" + password + "@github.com/MarekPetr/test";
        return executeBinary(gitPath, dest, gitOperation, url);
    }

    public String status(String dest) {
        String gitOperation = "status";
        return executeBinary(gitPath, dest, gitOperation);
    }

    public String addAllToStage(String dest) {
        String gitOperation = "add";
        return executeBinary(gitPath, dest, gitOperation, ".");
    }

    public String push(String dest) {
        String gitOperation = "push";
        return executeBinary(gitPath, dest, gitOperation);
    }

    public String pull(String dest) {
        String gitOperation = "pull";
        return executeBinary(gitPath, dest, gitOperation);
    }

}
