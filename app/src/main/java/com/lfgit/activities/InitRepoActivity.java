package com.lfgit.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.lfgit.R;
import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.databinding.ActivityInitRepoBinding;
import com.lfgit.tasks.GitExec;
import com.lfgit.view_models.LocalRepoViewModel;

public class InitRepoActivity extends BasicAbstractActivity {
    Button initButton;
    EditText initPathEditText;
    GitExec gitExec;

    private ActivityInitRepoBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_init_repo);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_init_repo);
        LocalRepoViewModel localRepoViewModel = ViewModelProviders.of(this).get(LocalRepoViewModel.class);
        mBinding.setLocalRepoViewModel(localRepoViewModel);
        mBinding.setLifecycleOwner(this);
        setupView();

        setupGit();
    }

    /*public void initButtonHandler(View view) {
        //String initPath = initPathEditText.getText().toString();
        if (gitExec.init()) {
            Repo repo = new Repo(initPath);
            RepoRepository repos = new RepoRepository(this);
            repos.insertRepo(repo);
        }
    }*/

    private void setupView() {
        initButton = findViewById(R.id.initButton);
        initPathEditText = findViewById(R.id.initPathEditText);
    }

    private void setupGit() {
        gitExec = new GitExec();
    }

    public void initButtonHandler(View view) {
    }
}
