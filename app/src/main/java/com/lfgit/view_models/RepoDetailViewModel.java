package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.executors.ExecCallback;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.Constants;

public class RepoDetailViewModel extends AndroidViewModel implements ExecCallback {
    private Repo mRepo;
    private MutableLiveData<String> taskResult = new MutableLiveData<>();
    private GitExec mGitExec;

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
        mGitExec = new GitExec(this);
    }

    @Override
    public void passResult(String result) {
        setTaskResult(result);
    }

    @Override
    public void passErrCode(int errCode, Constants.RepoTask task) {

    }

    public void setRepo(Repo repo) {
        mRepo = repo;
    }

    public MutableLiveData<String> getTaskResult() {
        return taskResult;
    }
    private void setTaskResult(String result) {
        taskResult.postValue(result);
    }

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
