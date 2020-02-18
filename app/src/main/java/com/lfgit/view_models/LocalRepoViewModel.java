package com.lfgit.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

public class LocalRepoViewModel extends ViewModel {
    private MutableLiveData<String> localRepoName;
    private GitExec gitExec = new GitExec();

    public void initLocalRepo(String localName) {
        localRepoName.setValue(localName);
        gitExec.init(localName);
    }
}
