package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

import static com.lfgit.utilites.Logger.LogMsg;

public class RepoDetailViewModel extends AndroidViewModel {
    private Repo mRepo;
    private MutableLiveData<String> taskResult = new MutableLiveData<>();
    GitExec gitExec = new GitExec();

    public RepoDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRepo(Repo repo) {
        mRepo = repo;
    }

    public void addAllToStage() {
        setTaskResult(gitExec.addAllToStage(mRepo.getLocalPath()));
    }

    public MutableLiveData<String> getTaskResult() {
        return taskResult;
    }

    private void setTaskResult(String result) {
        taskResult.setValue(result);
    }
}
