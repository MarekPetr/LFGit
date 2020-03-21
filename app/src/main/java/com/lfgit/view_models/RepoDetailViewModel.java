package com.lfgit.view_models;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;

public class RepoDetailViewModel extends ExecViewModel {
    private Repo mRepo;
    private MutableLiveData<String> mTaskResult = new MutableLiveData<>();

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onExecFinished(Constants.RepoTask task, String result, int errCode) {
        unsetPending();
        setTaskResult(result);
    }

    public void setRepo(Repo repo) {
        mRepo = repo;
    }

    public MutableLiveData<String> getTaskResult() {
        return mTaskResult;
    }
    private void setTaskResult(String result) {
        mTaskResult.postValue(result);
    }

    public MutableLiveData<Boolean> getExecPending() {return mExecPending;}

    public void execGitTask(int drawerPosition) {
        switch(drawerPosition) {
            case(0): gitAddAllToStage(); break;
            case(1): gitCommit();        break;
            case(2): gitPush();          break;
            case(3): gitPull();          break;
            case(4): gitStatus();        break;
            case(5): gitNewBranch();     break;
            case(6): gitAddRemote();     break;
            case(7): gitRemoveRemote();  break;
            case(8): gitMerge();         break;
        }
    }

    private String getRepoPath() { return mRepo.getLocalPath(); }

    private void gitAddAllToStage() { mGitExec.addAllToStage(getRepoPath()); }

    private void gitCommit() { mGitExec.commit(getRepoPath()); }

    private void gitPush() { mGitExec.push(getRepoPath()); }

    private void gitPull() { mGitExec.pull(getRepoPath()); }

    private void gitStatus() {mGitExec.status(getRepoPath());}

    private void gitNewBranch() {}

    private void gitAddRemote() {}

    private void gitRemoveRemote() {}

    private void gitMerge() {}
}
