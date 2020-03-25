package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.executors.ExecListener;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.TaskState;

import java.util.List;

import static com.lfgit.utilites.Constants.InnerState.FINISH;

public abstract class ExecViewModel extends AndroidViewModel implements ExecListener {
    GitExec mGitExec;
    RepoRepository mRepository;
    List<Repo> mAllRepos;
    MutableLiveData<Boolean> mExecPending = new MutableLiveData<>();

    ExecViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RepoRepository(application);
        mGitExec = new GitExec(this);
    }

    public void setAllRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }

    // background thread
    @Override
    public void onExecStarted(TaskState task) {
        if (task.getInnerState() == FINISH) {
            setPending();
        }
    }

    // background thread
    @Override
    public void onExecFinished(TaskState task, String result, int errCode) {
        if (task.getInnerState() == FINISH) {
            unsetPending();
        }
    }

    // background thread
    void setPending() {
        mExecPending.postValue(true);
    }

    // background thread
    void unsetPending() {
        mExecPending.postValue(false);
    }

    public MutableLiveData<Boolean> getExecPending() {
        return mExecPending;
    }

}
