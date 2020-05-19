package com.lfgit.executors;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Logger.LogMsg;

/**
 * Git commands
 */
public class GitExec {

    private String gitPath = "git";
    private String lfsPath = "git-lfs";
    private BinaryExecutor executor;

    public GitExec(ExecListener callback) {
        executor = new BinaryExecutor(callback);
    }


    /** Check if directory is a Git repository */
    public void isRepo(String path) {
        executor.run(gitPath, path, "rev-parse", "--git-dir");
    }

    /** Set Git Profile credentials */
    public void configCreds(String email, String username) {
        executor.run(gitPath, ".","config", "--global", "user.name", username);
        executor.run(gitPath, ".","config", "--global", "user.email", email);
    }

    /** Set Git Profile email */
    public void setEmail(String email) {
        executor.run(gitPath, ".","config", "--global", "user.email", email);
    }

    /** Set Git Profile username */
    public void setUsername(String username) {
        executor.run(gitPath, ".","config", "--global", "user.name", username);
    }

    /** Set hooks path */
    public void configHooks() {
        executor.run(gitPath, ".", "config", "--global", "core.hooksPath", HOOKS_DIR);
    }

    public void init(String localPath) {
        String gitOperation = "init";
        Constants.mkdirsIfNotExist(localPath);
        executor.run(gitPath, localPath, gitOperation);
    }

    public void commit(String localPath, String message) {
        String gitOperation = "commit";
        executor.run(gitPath, localPath, gitOperation, "-m", message);
    }

    public void clone(String localPath, String remoteURL) {
        String gitOperation = "clone";
        Constants.mkdirsIfNotExist(localPath);
        executor.run(gitPath, localPath, gitOperation, remoteURL);
    }

    public void shallowClone(String localPath, String remoteURL, String depth) {
        String gitOperation = "clone";
        executor.run(gitPath, localPath, gitOperation, "--depth", depth, remoteURL);
    }

    public void status(String localPath) {
        String gitOperation = "status";
        executor.run(gitPath, localPath, gitOperation);
    }

    public void addAllToStage(String localPath) {
        String gitOperation = "add";
        executor.run(gitPath, localPath, gitOperation, ".");
    }

    public void listBranches(String localPath) {
        executor.run(gitPath, localPath, "branch", "-a");
    }

    public void checkoutLocal(String localPath, String branch) {
        executor.run(gitPath, localPath, "checkout", branch);
    }

    public void checkoutRemote(String localPath, String branch) {
        executor.run(gitPath, localPath, "checkout", "--track", branch);
    }

    public void push(Repo repo) {
        ExecWithCredentials(repo,"push");
    }

    public void pull(Repo repo) {
        executor.run(gitPath, repo.getLocalPath(), "pull");
    }

    /**
     * Execute a command with address http(s)://username:password@domain
     * Handles URL encoding
     * */
    private void ExecWithCredentials(Repo repo, String... gitOperation) {
        String username = repo.getUsername();
        String password = repo.getPassword();
        try {
            username = URLEncoder.encode(repo.getUsername(), "UTF-8");
            password = URLEncoder.encode(repo.getPassword(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // try it without encoding
            LogMsg("Encoding failed");
        }
        String localPath = repo.getLocalPath();
        String remoteURL = repo.getRemoteURL();

        String regex = "://";
        String[] parts = remoteURL.split(regex);
        String scheme = parts[0]+"://";
        String domain = parts[1];
        String url = scheme + username + ":" + password + "@" + domain;
        executor.run(gitPath, localPath, gitOperation[0], url);
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

    public void resetHard(String localPath) {
        executor.run(gitPath, localPath, "reset", "--hard");
    }

    public void lfsPush(Repo repo) {
        ExecWithCredentials(repo, "push", "--all");
    }

    public void lfsPull(Repo repo) {
        executor.run(lfsPath, repo.getLocalPath(), "pull");
    }

    public void lfsInstall() {
        executor.run(lfsPath, ".", "install");
    }

    public void lfsTrackPattern(String localPath, String pattern) {
        executor.run(lfsPath, localPath, "track", pattern);
    }

    public void lfsUntrackPattern(String localPath, String pattern) {
        executor.run(lfsPath, localPath, "untrack", pattern);
    }

    public void lfsListPatterns(String localPath) {
        executor.run(lfsPath, localPath, "track");
    }

    public void lfsListFiles(String localPath) {
        executor.run(lfsPath, localPath, "ls-files");
    }

    public void lfsPrune(String localPath) {executor.run(lfsPath, localPath, "prune");}

    public void lfsEnv(String localPath) {
        executor.run(lfsPath, localPath, "env");
    }

    public void lfsStatus(String localPath) {
        executor.run(lfsPath, localPath, "status");
    }

}
