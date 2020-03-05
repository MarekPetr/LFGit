package com.lfgit.view_models;

import java.util.List;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;

public class RepoListViewModel extends AndroidViewModel {
    private RepoRepository mRepository;

    public RepoListViewModel(Application application) {
        super(application);
        mRepository = new RepoRepository(application);
    }
    public LiveData<List<Repo>> getAllRepos() {
        return mRepository.getAllRepos();
    }

    public void deleteRepoById(int id) {
        mRepository.deleteByID(id);
    }
}
