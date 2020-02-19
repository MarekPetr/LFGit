package com.lfgit.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lfgit.R;
import com.lfgit.database.RepoRepository;
import com.lfgit.database.model.Repo;
import com.lfgit.tasks.GitExec;

public class InitRepoActivity extends BasicAbstractActivity {
    Button initButton;
    EditText initPathEditText;
    GitExec gitExec;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_repo);

        setupView();
        setupGit();
    }

    public void initButtonHandler(View view) {
        String initPath = initPathEditText.getText().toString();
        if (gitExec.init(initPath)) {
            Repo repo = new Repo(initPath);
            RepoRepository repos = new RepoRepository(this);
            repos.insertRepo(repo);
        }
    }

    private void setupView() {
        initButton = findViewById(R.id.initButton);
        initPathEditText = findViewById(R.id.initPathEditText);
    }

    private void setupGit() {
        gitExec = new GitExec();
    }
}
