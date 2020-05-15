package com.lfgit.view_models;
import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.R;
import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;
import com.lfgit.utilites.UriHelper;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.lfgit.utilites.Constants.EXT_STORAGE;
import static com.lfgit.utilites.Constants.PendingTask.*;
import static com.lfgit.utilites.Constants.InnerState.*;

/**
 * Clone and Init repositories
 * */
public class AddRepoViewModel extends ExecViewModel {
    // data binding
    private String initRepoPath;
    private String cloneRepoPath;
    private String cloneURLPath;

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

    private Boolean repoAlreadyAdded(String path) {
        for (Repo repo : mAllRepos) {
            if (path.equals(repo.getLocalPath())) {
                setShowToast(getAppString(R.string.repoAlreadyAdded));
                return true;
            }
        }
        return false;
    }

    /** Handle a clone request */
    public void cloneRepoHandler() {
        if (StringUtils.isBlank(cloneRepoPath) || StringUtils.isBlank(cloneURLPath)) {
            setShowToast(getAppString(R.string.clone_prompt_info));
            return;
        }
        if (!cloneURLPath.startsWith("https://") && !cloneURLPath.startsWith("http://")) {
            setShowToast(getAppString(R.string.clone_enter_remote));
            return;
        }
        String fullRepoPath = getFullCloneRepoPath();
        if (!isInternalStorage(fullRepoPath)) return;
        if (repoAlreadyAdded(fullRepoPath)) return;

        Boolean isShallow = isShallowClone.getValue();
        if (isShallow != null && isShallow) {
            String depth = getDepth();
            if (StringUtils.isBlank(depth)) {
                setShowToast(getAppString(R.string.clone_enter_depth));
                return;
            }
            mState = new TaskState(FOR_USER, SHALLOW_CLONE);
            mGitExec.shallowClone(cloneRepoPath, cloneURLPath, depth);
        } else {
            mState = new TaskState(FOR_USER, CLONE);
            mGitExec.clone(cloneRepoPath, cloneURLPath);
        }
    }

    /** Handle an initialize request */
    public void initRepoHandler() {
        if (StringUtils.isBlank(initRepoPath)) {
            setShowToast(getAppString(R.string.init_enter_dir));
            return;
        }
        if (!isInternalStorage(initRepoPath)) return;
        if (repoAlreadyAdded(initRepoPath)) return;

        mState = new TaskState(FOR_USER, INIT);
        mGitExec.init(initRepoPath);
    }

    /** Check is given path is in the internal storage */
    private Boolean isInternalStorage(String path) {
        if (!path.startsWith(EXT_STORAGE)) {
            setShowToast(getAppString(R.string.add_internal_only));
            return false;
        }
        return true;
    }

    /** Process the result of the execution of a binary file */
    public void processExecResult(ExecResult execResult) {
        String result = execResult.getResult();
        int errCode = execResult.getErrCode();

        // Insert cloned or initialized repository to the database
        Constants.PendingTask pendingTask = mState.getPendingTask();
        if (pendingTask == CLONE || pendingTask == SHALLOW_CLONE) {
            insertClonedRepo(result, errCode);
        } else if (pendingTask == INIT) {
            insertInitRepo(result, errCode);
        }
    }

    /** Insert a cloned repository to the database */
    private void insertClonedRepo(String result, int errCode) {
        boolean successErrors = result.contains("Clone succeeded");
        if (errCode == 0 || successErrors) {
            String toastMsg = getAppString(R.string.clone_success);
            if (successErrors) {
                toastMsg = result;
            }

            // clone to directory of clone URL
            String fullRepoPath = getFullCloneRepoPath();
            Repo repo = new Repo(fullRepoPath, cloneURLPath);
            mRepository.insertRepo(repo);
            mCloneResult.setValue(toastMsg);
        } else {
            setShowToast(result);
        }
    }
    /** Insert an initialized repository to the database */
    private void insertInitRepo(String result, int errCode) {
        if (errCode == 0) {
            mRepository.insertRepo(new Repo(initRepoPath, getAppString(R.string.local_repo)));
            mInitResult.setValue(getAppString(R.string.init_success));
        } else {
            setShowToast(result);
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
        return (cloneRepoPath + "/" + UriHelper.getGitDir(cloneURLPath));
    }
    public String getCloneURLPath() {
        return cloneURLPath;
    }
    public void setCloneURLPath(String cloneURLPath) {
        this.cloneURLPath = cloneURLPath;
    }
    public MutableLiveData<Boolean> getIsShallowClone() {
        return isShallowClone;
    }
    public void setIsShallowClone(MutableLiveData<Boolean> value) {
        isShallowClone = value;
    }
    public void setDepth(String value) {
        depth = value;
    }
    public String getDepth() {
        return depth;
    }
}
