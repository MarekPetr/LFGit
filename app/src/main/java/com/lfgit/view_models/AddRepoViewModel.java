package com.lfgit.view_models;
import android.app.Application;
import android.net.Uri;
import android.os.Environment;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import static com.lfgit.utilites.Constants.AddRepo.*;
import static com.lfgit.utilites.Constants.Task.*;
import static com.lfgit.utilites.Constants.InnerState.*;

public class AddRepoViewModel extends ExecViewModel {
    // data binding
    private String initRepoPath;
    private String cloneRepoPath;
    private String cloneURLPath;

    private SingleLiveEvent<String> mCloneResult = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mInitResult = new SingleLiveEvent<>();


    public AddRepoViewModel(Application application) {
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
        if (!StringUtils.isBlank(cloneRepoPath) && !StringUtils.isBlank(cloneURLPath) ) {
            if (!isInternalStorage(cloneRepoPath)) return;
            mState = new TaskState(FOR_USER, CLONE);
            mGitExec.clone(cloneRepoPath, cloneURLPath);
        } else {
            setShowToast("Please enter remote URL and directory");
        }
    }

    public void initRepoHandler() {
        if (!StringUtils.isBlank(initRepoPath)) {
            if (!isInternalStorage(initRepoPath)) return;
            mState = new TaskState(FOR_USER, INIT);
            mGitExec.init(initRepoPath);
        } else {
            setShowToast("Please enter directory");
        }
    }

    private Boolean isInternalStorage(String path) {
        String mExtStorage = Environment.getExternalStorageDirectory().toString() + "/";
        if (!path.startsWith(mExtStorage)) {
            setShowToast("Please enter internal storage directory");
            return false;
        }
        return true;
    }

    // background thread
    @Override
    public void onExecFinished(String result, int errCode) {
        hidePendingOnRemoteUserTask(mState);

        if (mState.getPendingTask() == CLONE) {
            insertClonedRepo(errCode);
        } else if (mState.getPendingTask() == INIT) {
            insertInitRepo(errCode);
        }
    }

    // background thread
    private void insertClonedRepo(int errCode) {
        if (errCode == 0) {
            // clone to directory of clone URL
            String fullRepoPath = cloneRepoPath + "/" + UriHelper.getDirectory(cloneURLPath);
            Repo repo = new Repo(fullRepoPath, cloneURLPath);
            mRepository.insertRepo(repo);
            mCloneResult.postValue("Clone successful");
        } else {
            mCloneResult.postValue("Clone failed");
        }
    }

    // background thread
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
