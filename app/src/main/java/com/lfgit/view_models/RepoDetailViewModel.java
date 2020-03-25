package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.fragments.CredentialsDialog;
import com.lfgit.utilites.Constants;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import static com.lfgit.utilites.Constants.RepoTask.CONFIG;
import static com.lfgit.utilites.Constants.task.NONE;
import static com.lfgit.utilites.Constants.innerState.GET_REMOTE_GIT;
import static com.lfgit.utilites.Constants.task.PULL;
import static com.lfgit.utilites.Constants.innerState.START;
import static com.lfgit.utilites.Constants.task.PUSH;

public class RepoDetailViewModel extends ExecViewModel implements CredentialsDialog.CredentialsDialogListener{
    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();

    private TaskState mState = new TaskState(START, NONE);

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
    }

    // background thread
    @Override
    public void onExecFinished(Constants.RepoTask finishedTask, String result, int errCode) {
        if (finishedTask == CONFIG) {
            processTaskResult(result);
        } else {
            unsetPending();
            if (result.isEmpty()) {
                if (errCode == 0) {
                    setShowToast("Operation successful");
                } else {
                    setShowToast("Operation failed");
                }
            } else {
                setTaskResult(result);
            }
        }
    }

    // background thread
    private void processTaskResult(String result) {
        // Chop last end of line character
        String res = StringUtils.chop(result);
        if (mState.getTaskState() == GET_REMOTE_GIT) {
            if (res.isEmpty()) {
                postShowToast("Please add a remote");
            } else {
                mRepo.setRemoteURL(res);
                mRepository.updateRemoteURL(mRepo);
                if (mState.getPendingTask() == PULL) {
                    mGitExec.pull(mRepo);
                    mState.newState(START, NONE);
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
        mState.newState(START, PUSH);
        checkRepo();
    }

    private void gitPull() {
        mState.newState(START, PULL);
        checkRepo();
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

    private void checkRepo() {
        //CHECK_REMOTE_DB
        if (mRepo.getRemoteURL() != null) {
            //CHECK_CREDS_DB
            if (mRepo.getPassword() == null || mRepo.getUsername() == null) {
                setPromptCredentials(true);
            } else {
                pushOrPullAndFinish();
            }
        } else {
            // GET_REMOTE_GIT
            mState.setTaskState(GET_REMOTE_GIT);
            mGitExec.getRemoteURL(mRepo);
        }
    }

    @Override
    public void handleCredentials(String username, String password) {
        if (!StringUtils.isBlank(password) && !StringUtils.isBlank(username)) {
            setPromptCredentials(false);
            mRepo.setUsername(username);
            mRepo.setPassword(password);
            mRepository.updateCredentials(mRepo);
            pushOrPullAndFinish();
        } else {
            setShowToast("Please provide username and password");
        }
    }

    private void pushOrPullAndFinish() {
        if (mState.getPendingTask() == PULL) {
            mGitExec.pull(mRepo);
        } else if (mState.getPendingTask() == PUSH) {
            mGitExec.push(mRepo);
        }
        // FINISHED
        mState.newState(START, NONE);
    }

    @Override
    public void onCancelDialog() {
        // FINISHED
        mState.newState(START, NONE);
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

    public SingleLiveEvent<String> getShowToast() {
        return mShowToast;
    }

    private void setShowToast(String message) {
        mShowToast.setValue(message);
    }

    private void postShowToast(String message) {
        mShowToast.postValue(message);
    }
}
