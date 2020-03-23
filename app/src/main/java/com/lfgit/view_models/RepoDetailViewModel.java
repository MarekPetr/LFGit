package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import static com.lfgit.utilites.Constants.RepoTask.CONFIG;
import static com.lfgit.utilites.Constants.internalTask.GET_REMOTE;
import static com.lfgit.utilites.Constants.task.NONE;
import static com.lfgit.utilites.Constants.task.PULL;
import static com.lfgit.utilites.Logger.LogMsg;

public class RepoDetailViewModel extends ExecViewModel {
    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();
    private String mUsername;
    private String mPassword;

    private Constants.task pendingTask;
    private Constants.RepoTask lastTask;
    private Constants.internalTask interTask;

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
    }

    // background thread
    @Override
    public void onExecFinished(Constants.RepoTask task, String result, int errCode) {
        lastTask = task;
        if (task == CONFIG) {
            processTaskResult(result);
        } else {
            LogMsg("setting result");
            unsetPending();
            setTaskResult(result);
        }
    }

    // background thread
    private void processTaskResult(String result) {
        // Chop last end of line character
        String res = StringUtils.chop(result);
        if (lastTask == CONFIG) {
            if (interTask == GET_REMOTE) {
                mRepo.setRemoteURL(res);
                mRepository.updateRemoteURL(mRepo);
                if (pendingTask == PULL) {
                    mGitExec.pull(mRepo);
                    pendingTask = NONE;
                }
            }
        }
    }

    public void execGitTask(int drawerPosition) {
        if (drawerPosition == 0) {
            gitAddAllToStage();
        } else if (drawerPosition == 1) {
            gitCommit();
        } else if (drawerPosition == 2) {
            gitPush();
        } else if (drawerPosition == 3) {
            gitPull();
        } else if (drawerPosition == 4) {
            gitStatus();
        } else if (drawerPosition == 5) {
            gitNewBranch();
        } else if (drawerPosition == 6) {
            gitAddRemote();
        } else if (drawerPosition == 7) {
            gitRemoveRemote();
        } else if (drawerPosition == 8) {
            gitMerge();
        } else if (drawerPosition == 9) {
            setPromptCredentials(true);
        }
    }

    private void gitAddAllToStage() {
        mGitExec.addAllToStage(getRepoPath());
    }

    private void gitCommit() {
        mGitExec.commit(getRepoPath());
    }

    private void gitPush() {
        mGitExec.push(getRepoPath());
    }

    private void gitPull() {
        if (mRepo.getPassword() == null || mRepo.getUsername() == null) {
            setPromptCredentials(true);
            pendingTask = PULL;
        } else {
            pullCheckRemote();
        }
    }

    private void gitStatus() {
        mGitExec.status(getRepoPath());
    }

    private void gitNewBranch() {
    }

    private void gitAddRemote() {
    }

    private void gitRemoveRemote() {
    }

    private void gitMerge() {
    }

    private void setCredentialsExecPending() {
        if (!StringUtils.isBlank(mPassword) && !StringUtils.isBlank(mUsername)) {
            mRepo.setUsername(mUsername);
            mRepo.setPassword(mPassword);
            mRepository.updateCredentials(mRepo);
            if(pendingTask == PULL) {
                pullCheckRemote();
            }
            setPromptCredentials(false);
        }
    }
    
    private void pullCheckRemote() {
        if (mRepo.getRemoteURL() != null) {
            mGitExec.pull(mRepo);
            pendingTask = NONE;
        } else {
            interTask = GET_REMOTE;
            mGitExec.getRemoteURL(mRepo);
        }
    }

    public void handleCredentials(String username, String password) {
        setUsername(username);
        setPassword(password);
        setCredentialsExecPending();
    }

    public void setRepo(Repo repo) {
        mRepo = repo;
    }

    private String getRepoPath() {
        return mRepo.getLocalPath();
    }

    public MutableLiveData<String> getTaskResult() {
        return mTaskResult;
    }

    private void setTaskResult(String result) {
        mTaskResult.postValue(result);
    }

    public SingleLiveEvent<Boolean> getPromptCredentials() {
        return mPromptCredentials;
    }
    private void setPromptCredentials(Boolean value) {
        mPromptCredentials.setValue(value);
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public SingleLiveEvent<String> getShowToast() {
        return mShowToast;
    }

    public void setShowToast(String message) {
        mShowToast.setValue(message);
    }
}
