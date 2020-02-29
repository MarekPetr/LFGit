package com.lfgit.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;
import com.lfgit.utilites.BasicFunctions;

public class LocalRepoViewModel extends AndroidViewModel {
    private MutableLiveData<String> repoName = new MutableLiveData<>();
    private GitExec gitExec;
    private RepoRepository mRepository;

    public LocalRepoViewModel(Application application) {
        super(application);
        gitExec = new GitExec();
        mRepository = new RepoRepository(application);
    }

    // TODO check if repository already exists
    public boolean initLocalRepo() {
        // TODO get getReposPath from preferences
        String initPath = getRepoName();
        if (!gitExec.init(initPath)) {
            return false;
        }
        mRepository.insertRepo(new Repo(initPath));
        return true;
    }

    public void setRepoName(String repoName) {
        // TODO filePicker instead of repoPath
        String repoPath = BasicFunctions.getReposPath();
        this.repoName.setValue(repoPath + repoName);
    }

    public String getRepoName() {
        return repoName.getValue();
    }
}
