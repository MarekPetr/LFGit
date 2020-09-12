package com.lfgit.executors;

import android.app.Application;
import android.content.Context;

import com.lfgit.R;
import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.lfgit.utilites.Constants.HOOKS_DIR;
import static com.lfgit.utilites.Logger.LogDebugMsg;

/**
 * Git commands
 */
public class GitExec {
    private GitExecListener mGitExecListener;
    private BinaryExecutor mExecutor;
    private Context mContext;

    private String mGitPath = "git";
    private String mLfsPath = "git-lfs";


    public GitExec(ExecListener execCallback,
                   GitExecListener errorCallback,
                   Context context)
    {
        mExecutor = new BinaryExecutor(execCallback);
        mGitExecListener = errorCallback;
        mContext = context;
    }

    /** Check if directory is a Git repository */
    public void isRepo(String path) {
        mExecutor.run(mGitPath, path, "rev-parse", "--git-dir");
    }

    /** Set Git Profile credentials */
    public void configCreds(String email, String username) {
        mExecutor.run(mGitPath, ".","config", "--global", "user.name", username);
        mExecutor.run(mGitPath, ".","config", "--global", "user.email", email);
    }

    /** Set Git Profile email */
    public void setEmail(String email) {
        mExecutor.run(mGitPath, ".","config", "--global", "user.email", email);
    }

    /** Set Git Profile username */
    public void setUsername(String username) {
        mExecutor.run(mGitPath, ".","config", "--global", "user.name", username);
    }

    /** Set hooks path */
    public void configHooks() {
        mExecutor.run(mGitPath, ".", "config", "--global", "core.hooksPath", HOOKS_DIR);
    }

    public void init(String localPath) {
        String gitOperation = "init";
        Constants.mkdirsIfNotExist(localPath);
        mExecutor.run(mGitPath, localPath, gitOperation);
    }

    public void commit(String localPath, String message) {
        String gitOperation = "commit";
        mExecutor.run(mGitPath, localPath, gitOperation, "-m", message);
    }

    public void clone(String localPath, String remoteURL) {
        String gitOperation = "clone";
        Constants.mkdirsIfNotExist(localPath);
        mExecutor.run(mGitPath, localPath, gitOperation, remoteURL);
    }

    public void shallowClone(String localPath, String remoteURL, String depth) {
        String gitOperation = "clone";
        mExecutor.run(mGitPath, localPath, gitOperation, "--depth", depth, remoteURL);
    }

    public void status(String localPath) {
        String gitOperation = "status";
        mExecutor.run(mGitPath, localPath, gitOperation);
    }

    public void addAllToStage(String localPath) {
        String gitOperation = "add";
        mExecutor.run(mGitPath, localPath, gitOperation, ".");
    }

    public void listBranches(String localPath) {
        mExecutor.run(mGitPath, localPath, "branch", "-a");
    }

    public void checkoutLocal(String localPath, String branch) {
        mExecutor.run(mGitPath, localPath, "checkout", branch);
    }

    public void checkoutRemote(String localPath, String branch) {
        mExecutor.run(mGitPath, localPath, "checkout", "--track", branch);
    }

    public void push(Repo repo) {
        ExecWithCredentials(repo,"push");
    }

    public void pull(Repo repo) {
        mExecutor.run(mGitPath, repo.getLocalPath(), "pull");
    }

    /**
     * Execute a command with address http(s)://username:password@domain
     * Handles URL encoding
     * */
    private void ExecWithCredentials(Repo repo, String... gitOperation) {
        String remoteURL = repo.getRemoteURL();

        String httpRegex = "^https?://(?!.*//).*$";
        if (!remoteURL.matches(httpRegex)) {
            mGitExecListener.onError(mContext.getString(R.string.http_only));
            return;
        }

        String localPath = repo.getLocalPath();
        String username = repo.getUsername();
        String password = repo.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            mGitExecListener.onError(mContext.getString(R.string.no_creds));
            return;
        }

        try {
            username = URLEncoder.encode(repo.getUsername(), "UTF-8");
            password = URLEncoder.encode(repo.getPassword(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogDebugMsg("Encoding failed");
            mGitExecListener.onError(mContext.getString(R.string.encoding_creds_err));
            return;
        }

        String splitSeq = "://";
        String[] parts = remoteURL.split(splitSeq);
        if (parts.length != 2) {
            mGitExecListener.onError(mContext.getString(R.string.http_only));
            return;
        }
        
        String scheme = parts[0] + splitSeq;
        String domain = parts[1];
        String url = scheme + username + ":" + password + "@" + domain;
        mExecutor.run(mGitPath, localPath, gitOperation[0], url);
    }

    public void getRemoteURL(Repo repo) {
        String localPath = repo.getLocalPath();
        mExecutor.run(mGitPath, localPath, "config", "--get", "remote.origin.url");
    }

    public void addOriginRemote(Repo repo, String remoteURL) {
        String localPath = repo.getLocalPath();
        mExecutor.run(mGitPath, localPath, "remote", "add", "origin", remoteURL);
    }

    public void editOriginRemote(Repo repo, String remoteURL) {
        String localPath = repo.getLocalPath();
        mExecutor.run(mGitPath, localPath, "remote", "set-url", "origin", remoteURL);
    }
    
    public void log(String localPath) {
        mExecutor.run(mGitPath, localPath, "log");
    }

    public void resetHard(String localPath) {
        mExecutor.run(mGitPath, localPath, "reset", "--hard");
    }

    public void lfsPush(Repo repo) {
        ExecWithCredentials(repo, "push", "--all");
    }

    public void lfsPull(Repo repo) {
        mExecutor.run(mLfsPath, repo.getLocalPath(), "pull");
    }

    public void lfsInstall() {
        mExecutor.run(mLfsPath, ".", "install");
    }

    public void lfsTrackPattern(String localPath, String pattern) {
        mExecutor.run(mLfsPath, localPath, "track", pattern);
    }

    public void lfsUntrackPattern(String localPath, String pattern) {
        mExecutor.run(mLfsPath, localPath, "untrack", pattern);
    }

    public void lfsListPatterns(String localPath) {
        mExecutor.run(mLfsPath, localPath, "track");
    }

    public void lfsListFiles(String localPath) {
        mExecutor.run(mLfsPath, localPath, "ls-files");
    }

    public void lfsPrune(String localPath) {mExecutor.run(mLfsPath, localPath, "prune");}

    public void lfsEnv(String localPath) {
        mExecutor.run(mLfsPath, localPath, "env");
    }

    public void lfsStatus(String localPath) {
        mExecutor.run(mLfsPath, localPath, "status");
    }

}
