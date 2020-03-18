package com.lfgit.view_models;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.executors.GitExec;
import com.lfgit.utilites.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.lfgit.utilites.Constants.AddRepo.ADDED;
import static com.lfgit.utilites.Constants.AddRepo.OK;

public class LocalRepoViewModel extends AndroidViewModel {
    private GitExec gitExec;
    private RepoRepository mRepository;
    private List<Repo> mAllRepos;
    private String initRepoPath;

    private String cloneRepoPath;
    private String cloneURLPath;

    public LocalRepoViewModel(Application application) {
        super(application);
        gitExec = new GitExec();
        mRepository = new RepoRepository(application);
    }

    public void setAllRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }

    public boolean initLocalRepo() {
        if (!StringUtils.isBlank(initRepoPath)) {
            if (gitExec.init(initRepoPath) == 0) {
                mRepository.insertRepo(new Repo(initRepoPath));
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

    public boolean cloneRepo() {
        if (!StringUtils.isBlank(cloneRepoPath)) {
            Uri uri = Uri.parse(cloneURLPath);
            // get directory from URL
            String lastPathSegment = uri.getLastPathSegment();
            if (gitExec.clone(cloneRepoPath, cloneURLPath) == 0) {
                String fullRepoPath = cloneRepoPath + "/" + lastPathSegment;
                mRepository.insertRepo(new Repo(fullRepoPath));
                return true;
            }
        }
        return false;
    }

    public void setInitRepoPath(String name) {
        initRepoPath = name;
    }

    public String getInitRepoPath() {
        return initRepoPath;
    }

    public String getCloneRepoPath() {
        return cloneRepoPath;
    }

    public void setCloneRepoPath(String cloneRepoPath) {
        this.cloneRepoPath = cloneRepoPath;
    }

    public String getCloneURLPath() {
        return cloneURLPath;
    }

    public void setCloneURLPath(String cloneURLPath) {
        this.cloneURLPath = cloneURLPath;
    }
}
