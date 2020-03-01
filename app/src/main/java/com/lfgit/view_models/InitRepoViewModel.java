package com.lfgit.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;
import com.lfgit.utilites.BasicFunctions;

public class InitRepoViewModel extends AndroidViewModel {
    private String repoName;
    private GitExec gitExec;
    private RepoRepository mRepository;

    public InitRepoViewModel(Application application) {
        super(application);
        gitExec = new GitExec();
        mRepository = new RepoRepository(application);
    }

    // TODO check if repository already exists
    public boolean initLocalRepo() {
        // TODO get getReposPath from preferences
        String initPath = getRepoName();
        if (initPath != null) {
            if (gitExec.init(initPath)) {
                mRepository.insertRepo(new Repo(initPath));
                return true;
            }
        }
        return false;
    }

    public void setRepoName(String name) {
        // TODO filePicker instead of repoPath
        String repoPath = BasicFunctions.getReposPath();
        repoName = repoPath + name;
    }

    public String getRepoName() {
        return repoName;
    }
}
