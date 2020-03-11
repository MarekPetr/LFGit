package com.lfgit.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;
import com.lfgit.utilites.BasicFunctions;
import org.apache.commons.lang3.StringUtils;

public class LocalRepoViewModel extends AndroidViewModel {
    private GitExec gitExec;
    private RepoRepository mRepository;
    private String repoName;


    public LocalRepoViewModel(Application application) {
        super(application);
        gitExec = new GitExec();
        mRepository = new RepoRepository(application);
    }

    // TODO check if repository already exists
    public boolean initLocalRepo() {
        // TODO get getReposPath from preferences
        if (!StringUtils.isBlank(repoName)) {
            String initPath = BasicFunctions.getReposPath() + repoName;
            gitExec.init(initPath);
            if (gitExec.getErrCode() == 0) {
                mRepository.insertRepo(new Repo(initPath));
                return true;
            }
        }
        return false;
    }

    public void openLocalRepo(String path) {
        mRepository.insertRepo(new Repo(path));
    }

    public void setRepoName(String name) {
        // TODO filePicker instead of repoPath
        repoName = name;
    }

    public String getRepoName() {
        return repoName;
    }
}
