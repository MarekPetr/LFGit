package com.lfgit.executors;

import com.lfgit.database.model.Repo;

public class GitExec extends AbstractExecutor {

    private String gitPath = "git";

    public GitExec(ExecListener callback) {
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

    public void init(String dest) {
        String gitOperation = "init";
        executeBinary(gitPath, dest, gitOperation);
    }

    public void commit(String dest) {
        String gitOperation = "commit";
        String message = "-m \"newFileToCommit\"";
        executeBinary(gitPath, dest, gitOperation, message);
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

    public void pull(Repo repo) {
        String gitOperation = "pull";
        String regex = "://";
        String[] parts = repo.getRemoteURL().split(regex);
        String scheme = parts[0]+"://";
        String domain = parts[1];

        String url = scheme + repo.getUsername() + ":" + repo.getPassword() + "@" + domain;
        executeBinary(gitPath, repo.getLocalPath(), gitOperation, url);
    }

    public void getRemoteURL(Repo repo) {
        String localPath = repo.getLocalPath();
        executeBinary(gitPath, localPath, "config", "--get", "remote.origin.url");
    }
}
