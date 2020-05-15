package com.lfgit.view_models;

import java.util.List;
import java.util.Objects;
import android.app.Application;
import androidx.lifecycle.LiveData;

import com.lfgit.R;
import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.utilites.TaskState;

import static com.lfgit.utilites.Constants.InnerState.*;
import static com.lfgit.utilites.Constants.PendingTask.*;

/**
 * Manage list of repositories logic
 * */
public class RepoListViewModel extends ExecViewModel {
    private List<Repo> mAllRepos;
    private Repo mLastRepo;

    public RepoListViewModel(Application application) {
        super(application);
    }
    
    public LiveData<List<Repo>> getAllRepos() {
        return mRepository.getAllRepos();
    }

    public void setRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }

    public void deleteRepoById(int id) {
        mRepository.deleteByID(id);
    }

    /** Initiate addition of a local repository */
    public void addLocalRepo(String path) {
        // check if a path is valid, strip .git suffix
        if (path.endsWith("/.git")) {
            path = path.substring(0, path.length() - 5);
        } else if (path.contains("/.git/")) {
            setShowToast(getAppString(R.string.not_a_git_repo));
            return;
        }

        // check if repository is already added
        for (Repo repo : mAllRepos) {
            if (path.equals(repo.getLocalPath())) {
                setShowToast(getAppString(R.string.repoAlreadyAdded));
                return;
            }
        }

        // check if it is a Git repository
        mLastRepo = new Repo(path);
        mState = new TaskState(IS_REPO, NONE);
        mGitExec.isRepo(path);
    }

    public Boolean repoDirExists(Repo repo) {
        return mRepository.repoDirExists(repo);
    }

    /** Process the execution result */
    public void processExecResult(ExecResult execResult) {
        int errCode = execResult.getErrCode();
        String result = execResult.getResult();

        Constants.InnerState state = mState.getInnerState();
        if (state == IS_REPO) {
            // checking for a Git repository
            if (errCode == 0) {
                // get the repository remote URL
                mState = new TaskState(GET_REMOTE_GIT, NONE);
                mGitExec.getRemoteURL(mLastRepo);
            } else {
                setShowToast(getAppString(R.string.not_a_git_repo));
                mState = new TaskState(FOR_APP, NONE);
            }
        } else if (state == GET_REMOTE_GIT) {
            // parse a list of remote URLs, set the remote as the first found
            String[] resultLines = result.split(Objects.requireNonNull(System.getProperty("line.separator")));
            if (resultLines.length == 0 || errCode != 0) {
                mLastRepo.setRemoteURL(getAppString(R.string.local_repo));
            } else {
                mLastRepo.setRemoteURL(resultLines[0]);
            }
            mRepository.insertRepo(mLastRepo);
            mState = new TaskState(FOR_APP, NONE);
        }
    }
}
