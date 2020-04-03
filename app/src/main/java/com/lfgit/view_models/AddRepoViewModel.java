package com.lfgit.view_models;
import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.lfgit.utilites.Constants.EXT_STORAGE;
import static com.lfgit.utilites.Constants.Task.*;
import static com.lfgit.utilites.Constants.InnerState.*;

public class AddRepoViewModel extends ExecViewModel {
    // data binding
    private String initRepoPath;
    private String cloneRepoPath;
    private String cloneURLPath;

    public MutableLiveData<Boolean> getIsShallowClone() {
        return isShallowClone;
    }

    public void setIsShallowClone(MutableLiveData<Boolean> value) {
        isShallowClone = value;
    }

    private MutableLiveData<Boolean> isShallowClone = new MutableLiveData<>();
    private String depth;

    private List<Repo> mAllRepos;
    private SingleLiveEvent<String> mCloneResult = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mInitResult = new SingleLiveEvent<>();


    public AddRepoViewModel(Application application) {
        super(application);
        isShallowClone.setValue(false);
    }

    public LiveData<List<Repo>> getAllRepos() {
        return mRepository.getAllRepos();
    }

    public void setRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }

    private Boolean repoExists(String path) {
        for (Repo repo : mAllRepos) {
            if (path.equals(repo.getLocalPath())) {
                setShowToast("Repository already added");
                return true;
            }
        }
        return false;
    }

    public void cloneRepoHandler() {
        if (StringUtils.isBlank(cloneRepoPath) || StringUtils.isBlank(cloneURLPath)) {
            setShowToast("Please enter remote URL and directory");
            return;
        }
        if (!cloneURLPath.startsWith("https://") && !cloneURLPath.startsWith("http://")) {
            setShowToast("Enter http/https Remote URL");
            return;
        }
        String fullRepoPath = getFullCloneRepoPath();
        if (!isInternalStorage(fullRepoPath)) return;
        if (repoExists(fullRepoPath)) return;

        Boolean isShallow = isShallowClone.getValue();
        if (isShallow != null && isShallow) {
            String depth = getDepth();
            if (StringUtils.isBlank(depth)) {
                setShowToast("Please enter depth");
                return;
            }
            mState = new TaskState(FOR_USER, SHALLOW_CLONE);
            mGitExec.shallowClone(cloneRepoPath, cloneURLPath, depth);
        } else {
            mState = new TaskState(FOR_USER, CLONE);
            mGitExec.clone(cloneRepoPath, cloneURLPath);
        }
    }

    public void initRepoHandler() {
        if (StringUtils.isBlank(initRepoPath)) {
            setShowToast("Please enter directory");
            return;
        }
        if (!isInternalStorage(initRepoPath)) return;
        if (repoExists(initRepoPath)) return;

        mState = new TaskState(FOR_USER, INIT);
        mGitExec.init(initRepoPath);
    }

    private Boolean isInternalStorage(String path) {
        if (!path.startsWith(EXT_STORAGE)) {
            setShowToast("Please enter internal storage directory");
            return false;
        }
        return true;
    }

    // background thread
    @Override
    public void onExecFinished(String result, int errCode) {
        hidePendingIfNeeded(mState);
        Constants.Task pendingTask = mState.getPendingTask();
        if (pendingTask == CLONE || pendingTask == SHALLOW_CLONE) {
            insertClonedRepo(result, errCode);
        } else if (pendingTask == INIT) {
            insertInitRepo(result, errCode);
        }
    }

    // background thread
    private void insertClonedRepo(String result, int errCode) {
        if (errCode == 0) {
            // clone to directory of clone URL
            String fullRepoPath = getFullCloneRepoPath();
            Repo repo = new Repo(fullRepoPath, cloneURLPath);
            mRepository.insertRepo(repo);
            mCloneResult.postValue("Clone successful");
        } else {
            postShowToast(result);
        }
    }

    // background thread
    private void insertInitRepo(String result, int errCode) {
        if (errCode == 0) {
            mRepository.insertRepo(new Repo(initRepoPath));
            mInitResult.postValue("Repository initialized");
        } else {
            postShowToast(result);
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

    private String getFullCloneRepoPath() {
        return (cloneRepoPath + "/" + UriHelper.getDirectory(cloneURLPath));
    }
    public String getCloneURLPath() {
        return cloneURLPath;
    }
    public void setCloneURLPath(String cloneURLPath) {
        this.cloneURLPath = cloneURLPath;
    }


    public void setDepth(String value) {
        depth = value;
    }

    public String getDepth() {
        return depth;
    }
}
