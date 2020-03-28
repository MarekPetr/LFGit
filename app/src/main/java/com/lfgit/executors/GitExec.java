package com.lfgit.executors;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.TaskState;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.lfgit.utilites.Logger.LogMsg;

public class GitExec {

    private String gitPath = "git";
    private String lfsPath = "git-lfs";
    private BinaryExecutor executor;

    public GitExec(ExecListener callback) {
        executor = new BinaryExecutor(callback);
    }

    public void config(String email, String username) {
        executor.run(gitPath, ".","config", "--global", "user.name", username);
        executor.run(gitPath, ".","config", "--global", "user.email", email);
    }

    public void setEmail(String email) {
        executor.run(gitPath, ".","config", "--global", "user.email", email);
    }

    public void setUsername(String username) {
        executor.run(gitPath, ".","config", "--global", "user.name", username);
    }

    public void init(String localPath) {
        String gitOperation = "init";
        executor.run(gitPath, localPath, gitOperation);
    }

    public void commit(String localPath, String message) {
        String gitOperation = "commit";
        executor.run(gitPath, localPath, gitOperation, "-m", "\"" + message + "\"");
    }

    public void clone(String localPath, String remoteURL) {
        String gitOperation = "clone";
        executor.run(gitPath, localPath, gitOperation, remoteURL);
    }

    public void status(String localPath) {
        String gitOperation = "status";
        executor.run(gitPath, localPath, gitOperation);
    }

    public void addAllToStage(String localPath) {
        String gitOperation = "add";
        executor.run(gitPath, localPath, gitOperation, ".");
    }

    public void branch(String localPath) {
        executor.run(gitPath, localPath, "branch", "-a");
    }

    public void checkoutLocal(String localPath, String branch) {
        executor.run(gitPath, localPath, "checkout", branch);
    }

    public void checkoutRemote(String localPath, String branch) {
        executor.run(gitPath, localPath, "checkout", "--track", branch);
    }

    public void push(Repo repo) {
        pushOrPull("push", repo);
    }

    public void pull(Repo repo) {
        //pushOrPull("pull", repo);
        executor.run(gitPath, repo.getLocalPath(), "pull");
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
        executor.run(gitPath, localPath, gitOperation, url);
    }

    public void getRemoteURL(Repo repo) {
        String localPath = repo.getLocalPath();
        executor.run(gitPath, localPath, "config", "--get", "remote.origin.url");
    }

    public void addOriginRemote(Repo repo, String remoteURL) {
        String localPath = repo.getLocalPath();
        executor.run(gitPath, localPath, "remote", "add", "origin", remoteURL);
    }

    public void editOriginRemote(Repo repo, String remoteURL) {
        String localPath = repo.getLocalPath();
        executor.run(gitPath, localPath, "remote", "set-url", "origin", remoteURL);
    }
    
    public void log(String localPath) {
        executor.run(gitPath, localPath, "log");
    }

    public void lfsInstall(String localPath) {
        executor.run(lfsPath, localPath, "install");
    }
}
