package com.lfgit.executors;

import static com.lfgit.utilites.Constants.FILES_DIR;

public class GitExec extends AbstractExecutor {

    private String gitPath = "git";

    public GitExec(ExecCallback callback) {
        super(callback);
    }

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

    public void credentialHelperStore() {
        executeBinary(gitPath, ".", "config", "--global", "credential.helper", "store");
    }

    public int init(String dest) {
        String gitOperation = "init";
        executeBinary(gitPath, dest, gitOperation);
        return getErrCode();
    }

    public void commit(String dest) {
        String gitOperation = "commit";
        String message = "-m \"newFileToCommit\"";
        executeBinary(gitPath, dest, gitOperation, message);
    }

    public void cloneUname(String dest, String userName, String password) {
        String gitOperation = "clone";
        String url = "https://" + userName + ":" + password + "@github.com/MarekPetr/test";
        executeBinary(gitPath, dest, gitOperation, url);
    }

    public void clone(String dest, String remoteURL) {
        String gitOperation = "clone";
        executeBinary(gitPath, dest, gitOperation, remoteURL);
    }

    public void status(String dest) {
        String gitOperation = "status";
        executeBinary(gitPath, dest, gitOperation);
    }

    public void addAllToStage(String dest) {
        String gitOperation = "add";
        executeBinary(gitPath, dest, gitOperation, ".");
    }

    public void push(String dest) {
        String gitOperation = "push";
        executeBinary(gitPath, dest, gitOperation);
    }

    public void push_strace(String dest) {
        String gitOperation = "push";
        executeBinary("strace", dest, "/data/data/com.lfgit/files/usr/bin/git", "push");
    }

    public void pull(String dest) {
        String gitOperation = "pull";
        executeBinary(gitPath, dest, gitOperation);
    }
}
