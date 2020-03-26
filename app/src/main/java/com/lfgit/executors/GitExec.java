package com.lfgit.executors;

import android.net.Uri;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.TaskState;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.lfgit.utilites.Logger.LogMsg;

public class GitExec extends AbstractExecutor {

    private String gitPath = "git";

    public GitExec(ExecListener callback) {
        super(callback);
    }

    public void config(String email, String username, TaskState state) {
        executeBinary(state, gitPath, ".","config", "--global", "user.name", username);
        executeBinary(state, gitPath, ".","config", "--global", "user.email", email);
    }

    public void setEmail(String email, TaskState state) {
        executeBinary(state, gitPath, ".","config", "--global", "user.email", email);
    }

    public void setUsername(String username, TaskState state) {
        executeBinary(state, gitPath, ".","config", "--global", "user.name", username);
    }

    public void init(String dest, TaskState state) {
        String gitOperation = "init";
        executeBinary(state, gitPath, dest, gitOperation);
    }

    public void commit(String dest, TaskState state) {
        String gitOperation = "commit";
        String message = "-m \"newFileToCommit\"";
        executeBinary(state, gitPath, dest, gitOperation, message);
    }

    public void clone(String dest, String remoteURL, TaskState state) {
        String gitOperation = "clone";
        executeBinary(state, gitPath, dest, gitOperation, remoteURL);
    }

    public void status(String dest, TaskState state) {
        String gitOperation = "status";
        executeBinary(state, gitPath, dest, gitOperation);
    }

    public void addAllToStage(String dest, TaskState state) {
        String gitOperation = "add";
        executeBinary(state, gitPath, dest, gitOperation, ".");
    }

    public void push(Repo repo, TaskState state) {
        pushOrPull("push", repo, state);
    }

    public void pull(Repo repo, TaskState state) {
        pushOrPull("pull", repo, state);
    }

    private void pushOrPull(String gitOperation, Repo repo, TaskState state) {
        String username = repo.getUsername();
        String password = repo.getPassword();
        try {
            username = URLEncoder.encode(repo.getUsername(), "UTF-8");
            password = URLEncoder.encode(repo.getPassword(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // try it without encoding
            LogMsg("PushOrPull encoding failed");
        }
        String localPath = repo.getLocalPath();
        String remoteURL = repo.getRemoteURL();

        String regex = "://";
        String[] parts = remoteURL.split(regex);
        String scheme = parts[0]+"://";
        String domain = parts[1];
        String url = scheme + username + ":" + password + "@" + domain;
        executeBinary(state, gitPath, localPath, gitOperation, url);
    }

    public void getRemoteURL(Repo repo, TaskState state) {
        String localPath = repo.getLocalPath();
        executeBinary(state, gitPath, localPath, "config", "--get", "remote.origin.url");
    }

    public void addOriginRemote(Repo repo, String remoteURL, TaskState state) {
        String localPath = repo.getLocalPath();
        executeBinary(state, gitPath, localPath, "remote", "add", "origin", remoteURL);
    }

    public void editOriginRemote(Repo repo, String remoteURL, TaskState state) {
        String localPath = repo.getLocalPath();
        executeBinary(state, gitPath, localPath, "remote", "set-url", "origin", remoteURL);
    }
}
