package com.lfgit.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;

import java.util.List;

public class RepoListViewModel extends AndroidViewModel {
    private RepoRepository mRepository;
    private LiveData<List<Repo>> mAllRepos;

    public RepoListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RepoRepository(application);
    }
}
