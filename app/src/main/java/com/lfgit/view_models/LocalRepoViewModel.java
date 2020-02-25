package com.lfgit.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

public class LocalRepoViewModel extends AndroidViewModel {
    private MutableLiveData<String> repoPath = new MutableLiveData<>();
    private GitExec gitExec;
    private RepoRepository mRepository;

    public LocalRepoViewModel(Application application) {
        super(application);
        gitExec = new GitExec();
        mRepository = new RepoRepository(application);
    }

    // TODO check if repository already exists
    public boolean initLocalRepo() {
        String initPath = getRepoPath();
        if (!gitExec.init(initPath)) {
            return false;
        }
        repoPath.setValue(initPath);
        Repo repo = new Repo(initPath);
        mRepository.insertRepo(repo);
        return true;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath.setValue(repoPath);
    }

    public String getRepoPath() {
        return repoPath.getValue();
    }
}
