package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.fragments.dialogs.CommitDialog;
import com.lfgit.fragments.dialogs.RemoteDialog;
import com.lfgit.fragments.dialogs.CredentialsDialog;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

import static com.lfgit.utilites.Constants.InnerState.ADD_ORIGIN_REMOTE;
import static com.lfgit.utilites.Constants.InnerState.FINISH;
import static com.lfgit.utilites.Constants.InnerState.SET_ORIGIN_REMOTE;
import static com.lfgit.utilites.Constants.Task.ADD;
import static com.lfgit.utilites.Constants.Task.ADD_REMOTE;
import static com.lfgit.utilites.Constants.Task.COMMIT;
import static com.lfgit.utilites.Constants.Task.EDIT_REMOTE;
import static com.lfgit.utilites.Constants.Task.NONE;
import static com.lfgit.utilites.Constants.InnerState.GET_REMOTE_GIT;
import static com.lfgit.utilites.Constants.Task.PULL;
import static com.lfgit.utilites.Constants.InnerState.START;
import static com.lfgit.utilites.Constants.Task.PUSH;
import static com.lfgit.utilites.Constants.Task.STATUS;
import static com.lfgit.utilites.Logger.LogMsg;

public class RepoDetailViewModel extends ExecViewModel implements
        CredentialsDialog.CredentialsDialogListener,
        RemoteDialog.AddRemoteDialogListener,
        CommitDialog.CommitDialogListener
{

    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptRemote = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptCommit = new SingleLiveEvent<>();
    private SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();

    private TaskState mState = new TaskState(START, NONE);
    private String mTempRemoteURL;

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
            gitAddRemote();
        } else if (drawerPosition == 6) {
            gitSetRemote();
        } else if (drawerPosition == 7) {
            setPromptCredentials(true);
        }
    }

    private void gitAddAllToStage() {
        mState.newState(FINISH, ADD);
        mGitExec.addAllToStage(getRepoPath(), mState);
    }

    private void gitCommit() {
        mState.newState(START, COMMIT);
        setPromptCommit(true);        
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

    private void gitAddRemote() {
        mState.newState(START, ADD_REMOTE);
        setPromptRemote(true);
    }

    private void gitSetRemote() {
        mState.newState(START, EDIT_REMOTE);
        setPromptRemote(true);
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
            setShowToast("Please enter username and password");
        }
    }

    private void pushOrPullAndFinish() {
        if (mState.getPendingTask() == PULL) {
            pullAndFinish();
        } else if (mState.getPendingTask() == PUSH) {
            pushAndFinish();
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

    @Override
    public void onCancelCredentialsDialog() {
        // FINISHED
        mState.newState(START, NONE);
    }

    @Override
    public void handleRemoteURL(String remoteURL) {
        if (!StringUtils.isBlank(remoteURL)) {
            Constants.Task task = mState.getPendingTask();
            setPromptRemote(false);
            mTempRemoteURL = remoteURL;

            if (task == ADD_REMOTE) {
                mState.setInnerState(ADD_ORIGIN_REMOTE);
                mGitExec.addOriginRemote(mRepo, remoteURL, mState);
            } else if (task == EDIT_REMOTE){
                mState.setInnerState(SET_ORIGIN_REMOTE);
                mGitExec.editOriginRemote(mRepo, remoteURL, mState);
            }

        } else {
            setShowToast("Please enter remote URL");
        }
    }

    @Override
    public void onCancelAddRemoteDialog() {
        // FINISHED
        mState.newState(START, NONE);
    }

    @Override
    public void handleCommitMsg(String message) {
        if (!StringUtils.isBlank(message)) {
            setPromptCommit(false);
            mState.setInnerState(FINISH);
            mGitExec.commit(getRepoPath(), message, mState);
        } else {
            setShowToast("Please enter commit message");
        }
    }

    @Override
    public void onCancelCommitDialog() {
        // FINISHED
        mState.newState(START, NONE);
    }

    // background thread
    @Override
    public void onExecFinished(TaskState state, String result, int errCode) {
        mState = state;
        if (state.getInnerState() != FINISH) {
            processTaskResult(result, errCode);
        } else {
            postHidePendingOnRemoteFinish(state);
            if (result.isEmpty()) {
                if (errCode == 0) {
                    postShowToast("Operation successful");
                } else {
                    postShowToast("Operation failed");
                }
            } else {
                postTaskResult(result);
            }
            mState.newState(START, NONE);
        }
    }

    // background thread
    private void processTaskResult(String result, int errCode) {
        // get first remote URL from multiline result String
        String[] resultLines = result.split(Objects.requireNonNull(System.getProperty("line.separator")));

        Constants.InnerState innerState = mState.getInnerState();
        if (innerState == GET_REMOTE_GIT) {
            if (resultLines.length == 0) {
                postShowToast("Please add a remote");
            } else {
                mRepo.setRemoteURL(resultLines[0]);
                mRepository.updateRemoteURL(mRepo);
                if (!checkCredentialsDB()) {
                    postPromptCredentials(true);
                }
            }
        } else if (innerState == ADD_ORIGIN_REMOTE || innerState == SET_ORIGIN_REMOTE) {
            if (errCode != 0) {
                if (resultLines.length != 0) {
                    postTaskResult(result);
                } else {
                    postShowToast("Operation failed");
                }
            } else {
                mRepo.setRemoteURL(mTempRemoteURL);
                mRepository.updateRemoteURL(mRepo);
                if (innerState == ADD_ORIGIN_REMOTE) {
                    postShowToast("Remote origin added");
                } else {
                    postShowToast("Remote origin set");
                }
            }
            mState.newState(START, NONE);
        }
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
    private void postTaskResult(String result) {
        mTaskResult.postValue(result);
    }

    public SingleLiveEvent<Boolean> getPromptCredentials() {
        return mPromptCredentials;
    }
    private void setPromptCredentials(Boolean prompt) {
        mPromptCredentials.setValue(prompt);
    }
    private void postPromptCredentials(Boolean prompt) {
        mPromptCredentials.postValue(prompt);
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

    public SingleLiveEvent<Boolean> getPromptAddRemote() {
        return mPromptRemote;
    }

    public void setPromptRemote(Boolean prompt) {
        mPromptRemote.setValue(prompt);
    }

    public SingleLiveEvent<Boolean> getPromptCommit() {
        return mPromptCommit;
    }

    public void setPromptCommit(Boolean prompt) {
        mPromptCommit.setValue(prompt);
    }
}
