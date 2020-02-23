package com.lfgit.view_models;

import android.app.Application;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

public class LocalRepoViewModel extends AndroidViewModel {
    private MutableLiveData<String> repoPath;
    private RepoRepository repoRepository;
    private GitExec gitExec;

    public LocalRepoViewModel(Application application) {
        super(application);
        repoRepository = new RepoRepository(application);
        gitExec = new GitExec();
    }

    public void initLocalRepo(String localName) {
        repoPath.setValue(localName);
        gitExec.init(localName);
    }

    public RepoRepository getRepoRepo(){
        return repoRepository;
    }


}
