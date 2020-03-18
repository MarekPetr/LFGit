package com.lfgit.view_models;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.BasicFunctions;
import com.lfgit.utilites.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.lfgit.utilites.Constants.AddRepo.ADDED;
import static com.lfgit.utilites.Constants.AddRepo.OK;

public class LocalRepoViewModel extends AndroidViewModel {
    private GitExec gitExec;
    private RepoRepository mRepository;
    private String repoName;
    private List<Repo> mAllRepos;

    public LocalRepoViewModel(Application application) {
        super(application);
        gitExec = new GitExec();
        mRepository = new RepoRepository(application);
    }

    public boolean initLocalRepo() {
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

    public Constants.AddRepo addLocalRepo(String path) {
        for (Repo repo : mAllRepos) {
            if (path.equals(repo.getLocalPath())) {
                return ADDED;
            }
        }
        mRepository.insertRepo(new Repo(path));
        return OK;
    }

    public void setRepoName(String name) {
        repoName = name;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setAllRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }
    
    public boolean cloneRepo() {
        if (!StringUtils.isBlank(repoName)) {
            String initPath = BasicFunctions.getReposPath() + repoName;
            gitExec.clone(initPath);
            return gitExec.getErrCode() == 0;
        }
        return false;
    }
}
