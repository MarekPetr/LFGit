package com.lfgit.view_models;
import android.app.Application;
import android.net.Uri;
import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.lfgit.utilites.Constants.AddRepo.ALREADY_ADDED;
import static com.lfgit.utilites.Constants.AddRepo.OK;
import static com.lfgit.utilites.Constants.RepoTask.CLONE;
import static com.lfgit.utilites.Constants.RepoTask.INIT;

public class LocalRepoViewModel extends ExecViewModel {
    // data binding
    private String initRepoPath;
    private String cloneRepoPath;
    private String cloneURLPath;

    private SingleLiveEvent<String> mCloneResult = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mInitResult = new SingleLiveEvent<>();

    public LocalRepoViewModel(Application application) {
        super(application);
    }

    public Constants.AddRepo addLocalRepo(String path) {
        for (Repo repo : mAllRepos) {
            if (path.equals(repo.getLocalPath())) {
                return ALREADY_ADDED;
            }
        }
        mRepository.insertRepo(new Repo(path));
        return OK;
    }

    public void cloneRepoHandler() {
        if (!StringUtils.isBlank(cloneRepoPath)) {
            mGitExec.clone(cloneRepoPath, cloneURLPath);
        }
    }

    public void initRepoHandler() {
        if (!StringUtils.isBlank(initRepoPath)) {
            mGitExec.init(initRepoPath);
        }
    }

    @Override
    public void onExecFinished(Constants.RepoTask task, String result, int errCode) {
        mExecPending.postValue(false);
        if (task == CLONE) {
            insertClonedRepo(errCode);
        } else if (task == INIT) {
            insertInitRepo(errCode);
        }
    }

    private void insertClonedRepo(int errCode) {
        if (errCode == 0) {
            Uri uri = Uri.parse(cloneURLPath);
            // get directory from URL
            String lastPathSegment = uri.getLastPathSegment();
            String fullRepoPath = cloneRepoPath + "/" + lastPathSegment;
            Repo repo = new Repo(fullRepoPath, cloneURLPath);
            mRepository.insertRepo(repo);
            mCloneResult.postValue("Clone successful");
        } else {
            mCloneResult.postValue("Clone failed");
        }
    }

    private void insertInitRepo(int errCode) {
        if (errCode == 0) {
            mRepository.insertRepo(new Repo(initRepoPath));
            mInitResult.postValue("New repo " + initRepoPath + " initialized");
        } else {
            mInitResult.postValue("Init failed");
        }
    }

    public SingleLiveEvent<String> getCloneResult() {
        return mCloneResult;
    }
    public SingleLiveEvent<String> getInitResult() {
        return mInitResult;
    }
    public void setInitRepoPath(String name) {
        initRepoPath = name;
    }
    public String getInitRepoPath() {
        return initRepoPath;
    }
    public String getCloneRepoPath() {
        return cloneRepoPath;
    }
    public void setCloneRepoPath(String cloneRepoPath) {
        this.cloneRepoPath = cloneRepoPath;
    }
    public String getCloneURLPath() {
        return cloneURLPath;
    }
    public void setCloneURLPath(String cloneURLPath) {
        this.cloneURLPath = cloneURLPath;
    }
}
