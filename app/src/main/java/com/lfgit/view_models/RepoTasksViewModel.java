package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.fragments.dialogs.CommitDialog;
import com.lfgit.fragments.dialogs.RemoteDialog;
import com.lfgit.fragments.dialogs.CredentialsDialog;
import com.lfgit.utilites.Constants;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

import static com.lfgit.utilites.Constants.InnerState.ADD_ORIGIN_REMOTE;
import static com.lfgit.utilites.Constants.InnerState.FOR_USER;
import static com.lfgit.utilites.Constants.InnerState.SET_ORIGIN_REMOTE;
import static com.lfgit.utilites.Constants.Task.ADD;
import static com.lfgit.utilites.Constants.Task.ADD_REMOTE;
import static com.lfgit.utilites.Constants.Task.COMMIT;
import static com.lfgit.utilites.Constants.Task.EDIT_REMOTE;
import static com.lfgit.utilites.Constants.Task.LIST_BRANCHES;
import static com.lfgit.utilites.Constants.Task.NONE;
import static com.lfgit.utilites.Constants.InnerState.GET_REMOTE_GIT;
import static com.lfgit.utilites.Constants.Task.PULL;
import static com.lfgit.utilites.Constants.InnerState.FOR_APP;
import static com.lfgit.utilites.Constants.Task.PUSH;
import static com.lfgit.utilites.Constants.Task.STATUS;

public class RepoTasksViewModel extends ExecViewModel implements
        CredentialsDialog.CredentialsDialogListener,
        RemoteDialog.AddRemoteDialogListener,
        CommitDialog.CommitDialogListener
{

    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptRemote = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptCommit = new SingleLiveEvent<>();
    private String mTempRemoteURL;

    public RepoTasksViewModel(@NonNull Application application) {
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
            gitListBranches();
        }else if (drawerPosition == 8) {
            setPromptCredentials(true);
        }
    }

    private void gitAddAllToStage() {
        mState.newState(FOR_USER, ADD);
        mGitExec.addAllToStage(getRepoPath());
    }

    private void gitCommit() {
        mState.newState(FOR_APP, COMMIT);
        setPromptCommit(true);        
    }

    private void gitPush() {
        mState.newState(FOR_APP, PUSH);
        get_remote_git();
    }

    private void gitPull() {
        mState.newState(FOR_APP, PULL);
        get_remote_git();
    }

    private void gitStatus() {
        mState.newState(FOR_USER, STATUS);
        mGitExec.status(getRepoPath());
    }

    private void gitAddRemote() {
        mState.newState(FOR_APP, ADD_REMOTE);
        setPromptRemote(true);
    }

    private void gitSetRemote() {
        mState.newState(FOR_APP, EDIT_REMOTE);
        setPromptRemote(true);
    }

    private void gitListBranches() {
        mState.newState(FOR_USER, LIST_BRANCHES);

    }

    private void get_remote_git() {
        mState.setInnerState(GET_REMOTE_GIT);
        mGitExec.getRemoteURL(mRepo);
    }

    private Boolean credentialsSetDB() {
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
        mState.newState(FOR_USER, PUSH);
        mGitExec.push(mRepo);
    }

    private void pullAndFinish() {
        mState.newState(FOR_USER, PULL);
        mGitExec.pull(mRepo);
    }

    @Override
    public void onCancelCredentialsDialog() {
        // FINISHED
        mState.newState(FOR_APP, NONE);
    }

    @Override
    public void handleRemoteURL(String remoteURL) {
        if (!StringUtils.isBlank(remoteURL)) {
            Constants.Task task = mState.getPendingTask();
            setPromptRemote(false);
            mTempRemoteURL = remoteURL;

            if (task == ADD_REMOTE) {
                mState.setInnerState(ADD_ORIGIN_REMOTE);
                mGitExec.addOriginRemote(mRepo, remoteURL);
            } else if (task == EDIT_REMOTE){
                mState.setInnerState(SET_ORIGIN_REMOTE);
                mGitExec.editOriginRemote(mRepo, remoteURL);
            }

        } else {
            setShowToast("Please enter remote URL");
        }
    }

    @Override
    public void onCancelAddRemoteDialog() {
        // FINISHED
        mState.newState(FOR_APP, NONE);
    }

    @Override
    public void handleCommitMsg(String message) {
        if (!StringUtils.isBlank(message)) {
            setPromptCommit(false);
            mState.setInnerState(FOR_USER);
            mGitExec.commit(getRepoPath(), message);
        } else {
            setShowToast("Please enter commit message");
        }
    }

    @Override
    public void onCancelCommitDialog() {
        // FINISHED
        mState.newState(FOR_APP, NONE);
    }

    // background thread
    @Override
    public void onExecFinished(String result, int errCode) {
        if (mState.getInnerState() != FOR_USER) {
            processTaskResult(result, errCode);
        } else {
            hidePendingOnRemoteUserTask(mState);
            if (result.isEmpty()) {
                if (errCode == 0) {
                    postShowToast("Operation successful");
                } else {
                    postShowToast("Operation failed");
                }
            } else {
                postTaskResult(result);
            }
            mState.newState(FOR_APP, NONE);
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
                if (!credentialsSetDB()) {
                    postPromptCredentials(true);
                } else {
                    pushOrPullAndFinish();
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
            mState.newState(FOR_APP, NONE);
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
