package com.lfgit.view_models;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.fragments.dialogs.CheckoutDialog;
import com.lfgit.fragments.dialogs.CommitDialog;
import com.lfgit.fragments.dialogs.PatternDialog;
import com.lfgit.fragments.dialogs.RemoteDialog;
import com.lfgit.fragments.dialogs.CredentialsDialog;
import com.lfgit.utilites.Constants;
import com.lfgit.view_models.Events.SingleLiveEvent;

import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

import static com.lfgit.utilites.Constants.InnerState.*;
import static com.lfgit.utilites.Constants.Task.*;

public class RepoTasksViewModel extends ExecViewModel implements
        CredentialsDialog.CredentialsDialogListener,
        RemoteDialog.AddRemoteDialogListener,
        CommitDialog.CommitDialogListener,
        CheckoutDialog.CheckoutDialogListener,
        PatternDialog.PatternDialogListener
{

    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> mPromptCredentials = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptRemote = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptCommit = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptCheckout = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> mPromptPattern = new SingleLiveEvent<>();
    private String mTempRemoteURL;

    public RepoTasksViewModel(@NonNull Application application) {
        super(application);
    }

    interface GitAction {
        void execute();
    }

    private GitAction[] tasks = new GitAction[] {
            this::gitAddAllToStage,
            this::gitCommit,
            this::gitPush,
            this::gitPull,
            this::gitStatus,
            this::gitLog,
            this::gitResetHard,
            this::gitAddRemote,
            this::gitSetRemote,
            this::gitListBranches,
            this::gitCheckoutLocal,
            this::gitCheckoutRemote,
            this::lfsInstall,
            this::lfsTrackPattern,
            this::lfsUntrackPattern,
            this::lfsListPatterns,
            this::lfsListFiles,
            this::lfsEnv,
            () -> {
                mState.newState(FOR_USER, NONE);
                setPromptCheckout(true);
            },
    };

    public void execGitTask(int drawerPosition) {
        tasks[drawerPosition].execute();
    }

    private void gitAddAllToStage() {
        mState.newState(FOR_USER, ADD);
        //mGitExec.addAllToStage(getRepoPath());
        mGitExec.straceAdd(getRepoPath());

    }

    private void gitCommit() {
        mState.newState(FOR_APP, COMMIT);
        setPromptCommit(true);        
    }

    private void gitPush() {
        mState.newState(FOR_APP, PUSH);
        getRemoteGit();
    }

    private void gitPull() {
        mState.newState(FOR_APP, PULL);
        getRemoteGit();
    }

    private void gitStatus() {
        mState.newState(FOR_USER, STATUS);
        mGitExec.status(getRepoPath());
    }

    private void gitLog() {
        mState.newState(FOR_USER, LOG);
        mGitExec.log(getRepoPath());
    }

    private void gitResetHard() {
        mState.newState(FOR_USER, RESET_HARD);
        mGitExec.resetHard(getRepoPath());
    }

    private void gitAddRemote() {
        mState.newState(FOR_APP, ADD_REMOTE);
        setPromptRemote(true);
    }

    private void gitSetRemote() {
        mState.newState(FOR_APP, SET_REMOTE);
        setPromptRemote(true);
    }

    private void gitListBranches() {
        mState.newState(FOR_USER, LIST_BRANCHES);
        mGitExec.listBranches(getRepoPath());
    }

    private void gitCheckoutLocal() {
        mState.newState(FOR_USER, CHECKOUT_LOCAL);
        setPromptCheckout(true);
    }

    private void gitCheckoutRemote() {
        mState.newState(FOR_USER, CHECKOUT_REMOTE);
        setPromptCheckout(true);
    }

    private void lfsInstall() {
        mState.newState(FOR_USER, LFS_INSTALL);
        mGitExec.lfsInstall(getRepoPath());
    }

    private void lfsTrackPattern() {
        mState.newState(FOR_USER, LFS_TRACK);
        setPromptPattern(true);
    }

    private void lfsUntrackPattern() {
        mState.newState(FOR_USER, LFS_UNTRACK);
        setPromptPattern(true);
    }

    private void lfsListPatterns() {
        mState.newState(FOR_USER, LFS_LIST_PATTERNS);
        mGitExec.lfsListPatterns(getRepoPath());
    }

    private void lfsListFiles() {
        mState.newState(FOR_USER, LFS_LIST_FILES);
        mGitExec.lfsListFiles(getRepoPath());
    }

    private void lfsEnv() {
        mState.newState(FOR_USER, LFS_ENV);
        mGitExec.lfsEnv(getRepoPath());
    }


    /*********************************************************************/

    private void getRemoteGit() {
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
            pushPendingAndFinish();
        } else {
            setShowToast("Please enter username and password");
        }
    }
    @Override
    public void onCancelCredentialsDialog() {
        startState();
    }

    private void pushPendingAndFinish() {
        if (mState.getPendingTask() == PUSH) {
            mState.newState(FOR_USER, PUSH);
            mGitExec.push(mRepo);
        }
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
            } else if (task == SET_REMOTE){
                mState.setInnerState(SET_ORIGIN_REMOTE);
                mGitExec.editOriginRemote(mRepo, remoteURL);
            }

        } else {
            setShowToast("Please enter remote URL");
        }
    }

    @Override
    public void onCancelAddRemoteDialog() {
        startState();
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
        startState();
    }

    @Override
    public void handleCheckoutBranch(String branch) {
        if (!StringUtils.isBlank(branch)) {
            setPromptCheckout(false);
            mState.setInnerState(FOR_USER);
            if (mState.getPendingTask() == CHECKOUT_LOCAL) {
                mGitExec.checkoutLocal(getRepoPath(), branch);
            } else {
                mGitExec.checkoutRemote(getRepoPath(), branch);
            }
        } else {
            setShowToast("Please enter branch");
        }
    }

    @Override
    public void onCancelCheckoutDialog() {
        startState();
    }

    private void startState() {
        mState.newState(FOR_USER, NONE);
    }

    @Override
    public void handlePattern(String pattern) {
        if (!StringUtils.isBlank(pattern)) {
            setPromptPattern(false);
            mState.setInnerState(FOR_USER);
            if (mState.getPendingTask() == LFS_TRACK) {
                mGitExec.lfsTrackPattern(getRepoPath(), pattern);
            } else {
                mGitExec.lfsUntrackPattern(getRepoPath(), pattern);
            }
        } else {
            setShowToast("Please enter pattern");
        }
    }

    @Override
    public void onCancelPatternDialog() {
        startState();
    }

    // background thread
    @Override
    public void onExecFinished(String result, int errCode) {
        if (mState.getInnerState() != FOR_USER) {
            processTaskResult(result, errCode);
        } else {
            hidePendingIfNeeded(mState);
            if (result.isEmpty()) {
                if (errCode == 0) {
                    postTaskResult("Operation successful");
                } else {
                    postTaskResult("Operation failed");
                }
            } else {
                postTaskResult(result);
            }
            mState.newState(FOR_USER, NONE);
        }
    }

    // background thread
    private void processTaskResult(String result, int errCode) {
        // get first remote URL from multiline result String
        String[] resultLines = result.split(Objects.requireNonNull(System.getProperty("line.separator")));

        Constants.InnerState innerState = mState.getInnerState();
        if (innerState == GET_REMOTE_GIT) {
            if (resultLines.length == 0 || errCode != 0) {
                mRepo.setRemoteURL("Local Repository");
                mRepository.updateRemoteURL(mRepo);
                postShowToast("Please add a remote");
            } else {
                mRepo.setRemoteURL(resultLines[0]);
                mRepository.updateRemoteURL(mRepo);
                if (mState.getPendingTask() == PULL) {
                    mState.newState(FOR_USER, NONE);
                    mGitExec.pull(mRepo);
                } else {
                    if (!credentialsSetDB()) {
                        postPromptCredentials(true);
                    } else {
                        pushPendingAndFinish();
                    }
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

    public SingleLiveEvent<Boolean> getPromptCheckout() {
        return mPromptCheckout;
    }
    public void setPromptCheckout(Boolean prompt) {
        mPromptCheckout.setValue(prompt);
    }
    public SingleLiveEvent<Boolean> getPromptPattern() {
        return mPromptPattern;
    }
    public void setPromptPattern(Boolean prompt) {
        mPromptPattern.setValue(prompt);
    }
}
