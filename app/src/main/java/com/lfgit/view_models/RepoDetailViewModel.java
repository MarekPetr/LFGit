package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

public class RepoDetailViewModel extends AndroidViewModel {
    private Repo mRepo;
    private MutableLiveData<String> taskResult = new MutableLiveData<>();
    private GitExec gitExec = new GitExec();

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRepo(Repo repo) {
        mRepo = repo;
    }

    public MutableLiveData<String> getTaskResult() {
        return taskResult;
    }
    private void setTaskResult(String result) {
        taskResult.setValue(result);
    }

    public void execGitTask(int position) {
        String result = "";
        switch(position) {
            case(0): result = gitAddAllToStage(); break;
            case(1): result = gitCommit();        break;
            case(2): result = gitPush();          break;
            case(3): result = gitPull();          break;
            case(4): result = gitStatus();        break;
            case(5): result = gitNewBranch();     break;
            case(6): result = gitAddRemote();     break;
            case(7): result = gitRemoveRemote();  break;
            case(8): result = gitMerge();         break;
        }
        setTaskResult(result);
    }

    private String gitAddAllToStage() {
        return (gitExec.addAllToStage(mRepo.getLocalPath()));
    }

    private String gitCommit() {
        return gitExec.commit(mRepo.getLocalPath());
    }

    private String gitPush() {
        return gitExec.push(mRepo.getLocalPath());
    }

    private String gitPull() {
        return gitExec.pull(mRepo.getLocalPath());
    }

    private String gitStatus() {return gitExec.status(mRepo.getLocalPath());}

    private String gitNewBranch() {return null;}

    private String gitAddRemote() {return null;}

    private String gitRemoveRemote() {return null;}

    private String gitMerge() {return null;}


}
