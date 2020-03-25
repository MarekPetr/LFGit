package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.fragments.CredentialsDialog;
import com.lfgit.utilites.TaskState;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.lfgit.utilites.Constants.InnerState.FINISH;
import static com.lfgit.utilites.Constants.Task.ADD;
import static com.lfgit.utilites.Constants.Task.NONE;
import static com.lfgit.utilites.Constants.InnerState.GET_REMOTE_GIT;
import static com.lfgit.utilites.Constants.Task.PULL;
import static com.lfgit.utilites.Constants.InnerState.START;
import static com.lfgit.utilites.Constants.Task.PUSH;
import static com.lfgit.utilites.Constants.Task.STATUS;
import static com.lfgit.utilites.Logger.LogMsg;

public class RepoDetailViewModel extends ExecViewModel implements CredentialsDialog.CredentialsDialogListener{
    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();

    private TaskState mState = new TaskState(START, NONE);

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
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
        mState.newState(FINISH, ADD);
        mGitExec.addAllToStage(getRepoPath(), mState);
    }

    private void gitCommit() {
        mState.newState(FINISH, PUSH);
        mGitExec.commit(getRepoPath(), mState);
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
        mState.newState(FINISH, STATUS);
        mGitExec.status(getRepoPath(), mState);
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
            if(checkCredentialsDB()) {
                pushOrPullAndFinish();
            } else {
                setPromptCredentials(true);
            }
        } else {
            // GET_REMOTE_GIT
            mState.setInnerState(GET_REMOTE_GIT);
            // Is a config command, so continues in processTaskResult.
            mGitExec.getRemoteURL(mRepo, mState);
        }
    }

    private Boolean checkCredentialsDB() {
        return mRepo.getPassword() != null && mRepo.getUsername() != null;
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

    @Override
    public void onCancelCredentialsDialog() {
        // FINISHED
        mState.newState(START, NONE);
    }

    private void pushOrPullAndFinish() {
        if (mState.getPendingTask() == PULL) {
            pullAndFinish();
        } else if (mState.getPendingTask() == PUSH) {
            pushAndFinish();
        }
    }

    // background thread
    @Override
    public void onExecFinished(TaskState state, String result, int errCode) {
        if (state.getInnerState() != FINISH) {
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
            mState.newState(START, NONE);
        }
    }

    // background thread
    private void processTaskResult(String result) {
        // get first remote URL from multiline result String
        String[] lines = result.split(Objects.requireNonNull(System.getProperty("line.separator")));

        if (mState.getInnerState() == GET_REMOTE_GIT) {
            if (lines.length == 0) {
                postShowToast("Please add a remote");
            } else {
                mRepo.setRemoteURL(lines[0]);
                mRepository.updateRemoteURL(mRepo);
                if (!checkCredentialsDB()) {
                    postPromptCredentials(true);
                }
            }
        }
    }

    private void pushAndFinish() {
        mState.newState(FINISH, PUSH);
        mGitExec.push(mRepo, mState);
    }

    private void pullAndFinish() {
        mState.newState(FINISH, PULL);
        mGitExec.pull(mRepo, mState);
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
    private void postPromptCredentials(Boolean value) {
        mPromptCredentials.postValue(value);
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
