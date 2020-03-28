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

    public void commit(String dest, String message) {
        String gitOperation = "commit";
        executeBinary(gitPath, dest, gitOperation, "-m", "\"" + message + "\"");
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

    public void branch(String dest) {
        executeBinary(gitPath, dest, "branch", "-a");
    }

    public void checkoutLocal(String dest, String branch) {
        executeBinary(gitPath, dest, "checkout", branch);
    }

    public void checkoutRemote(String dest, String branch) {
        executeBinary(gitPath, dest, "checkout", "--track", branch);
    }

    public void push(Repo repo) {
        pushOrPull("push", repo);
    }

    public void pull(Repo repo) {
        //pushOrPull("pull", repo);
        executeBinary(gitPath, repo.getLocalPath(), "pull");
    }

    private void pushOrPull(String gitOperation, Repo repo) {
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
        executeBinary(gitPath, localPath, gitOperation, url);
    }

    public void getRemoteURL(Repo repo) {
        String localPath = repo.getLocalPath();
        executeBinary(gitPath, localPath, "config", "--get", "remote.origin.url");
    }

    public void addOriginRemote(Repo repo, String remoteURL) {
        String localPath = repo.getLocalPath();
        executeBinary(gitPath, localPath, "remote", "add", "origin", remoteURL);
    }

    public void editOriginRemote(Repo repo, String remoteURL) {
        String localPath = repo.getLocalPath();
        executeBinary(gitPath, localPath, "remote", "set-url", "origin", remoteURL);
    }
}
