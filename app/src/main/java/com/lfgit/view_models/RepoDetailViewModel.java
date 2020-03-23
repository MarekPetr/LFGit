package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import static com.lfgit.utilites.Logger.LogMsg;

public class RepoDetailViewModel extends ExecViewModel {
    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();

    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private String mUsername;
    private String mPassword;

    private Constants.task lastTask;

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onExecFinished(Constants.RepoTask task, String result, int errCode) {
        unsetPending();
        setTaskResult(result);
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
            lastTask = Constants.task.PULL;
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
    public void setPromptCredentials(Boolean value) {
        mPromptCredentials.setValue(value);
    }

    private void executeTask() {
        if (!StringUtils.isBlank(mPassword) && !StringUtils.isBlank(mUsername)) {
            mRepo.setUsername(mUsername);
            mRepo.setPassword(mPassword);
            mRepository.updateCredentials(mRepo);
            if(lastTask == Constants.task.PULL) {
                LogMsg("PULL");
                if (mRepo.getRemoteURL() != null) {
                    mGitExec.pull(mRepo);
                } else {
                    // TODO toast
                    LogMsg("No remote URL");
                }
            }
            setPromptCredentials(false);
        }
    }

    public void handleCredentials(String username, String password) {
        setUsername(username);
        setPassword(password);
        executeTask();
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
}
