package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

import java.util.List;

public class RepoListViewModel extends AndroidViewModel {
    private LiveData<List<Repo>> mAllRepos;
    private RepoRepository mRepository;

    public RepoListViewModel(Application application) {
        super(application);
        mRepository = new RepoRepository(application);
    }
    public RepoRepository getRepoRepository(){
        return mRepository;
    }

    public LiveData<List<Repo>> getAllRepos() {
        return mRepository.getAllRepos();
    }
}
