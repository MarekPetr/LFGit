package com.lfgit.view_models;

import java.util.List;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.utilites.Constants;
import com.lfgit.view_models.Events.SingleLiveEvent;

import static com.lfgit.utilites.Constants.AddRepo.ALREADY_ADDED;
import static com.lfgit.utilites.Constants.AddRepo.OK;

public class RepoListViewModel extends AndroidViewModel {
    private RepoRepository mRepository;
    private List<Repo> mAllRepos;
    public SingleLiveEvent<String> mShowToast = new SingleLiveEvent<>();

    public RepoListViewModel(Application application) {
        super(application);
        mRepository = new RepoRepository(application);
    }
    public LiveData<List<Repo>> getAllRepos() {
        return mRepository.getAllRepos();
    }

    public void setAllRepos(List<Repo> repoList) {
        mAllRepos = repoList;
    }

    public void deleteRepoById(int id) {
        mRepository.deleteByID(id);
    }

    public void addLocalRepo(String path) {
        for (Repo repo : mAllRepos) {
            if (path.equals(repo.getLocalPath())) {
                setShowToast("Repository already added");
                return;
            }
        }
        mRepository.insertRepo(new Repo(path));
    }

    private void setShowToast(String message) {
        mShowToast.setValue(message);
    }
    public SingleLiveEvent<String> getShowToast() {
        return mShowToast;
    }
}
