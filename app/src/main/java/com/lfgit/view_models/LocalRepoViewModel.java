package com.lfgit.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.tasks.GitExec;

import static com.lfgit.utilites.Logger.LogMsg;

public class LocalRepoViewModel extends AndroidViewModel {
    private MutableLiveData<String> repoPath = new MutableLiveData<>();
    private RepoRepository repoRepository;
    private GitExec gitExec;

    public LocalRepoViewModel(Application application) {
        super(application);
        repoRepository = new RepoRepository(application);
        gitExec = new GitExec();
    }

    // TODO check if repository already exists
    public void initLocalRepo() {
        String initPath = getRepoPath();
        if (gitExec.init(initPath)) {
            repoPath.setValue(initPath);
            /*Repo repo = new Repo(initPath);
            repoRepository.insertRepo(repo);
            gitExec.init(initPath);*/
        }
    }

    public RepoRepository getRepoRepository(){
        return repoRepository;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath.setValue(repoPath);
    }

    public String getRepoPath() {
        return repoPath.getValue();
    }
}
