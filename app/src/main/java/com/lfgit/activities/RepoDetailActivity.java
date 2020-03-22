package com.lfgit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.adapters.RepoOperationsAdapter;
import com.lfgit.database.model.Repo;
import com.lfgit.databinding.ActivityRepoDetailBinding;
import com.lfgit.view_models.RepoDetailViewModel;

public class RepoDetailActivity extends BasicAbstractActivity {
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ActivityRepoDetailBinding mBinding;
    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_detail);
        RepoDetailViewModel viewModel = new ViewModelProvider(this).get(RepoDetailViewModel.class);
        mBinding.setRepoDetailViewModel(viewModel);
        mBinding.setLifecycleOwner(this);

        setupDrawer(viewModel);
        mBinding.taskResult.setMovementMethod(new ScrollingMovementMethod());

        Repo repo = (Repo) getIntent().getSerializableExtra(Repo.TAG);
        viewModel.setRepo(repo);

        viewModel.getExecPending().observe(this, isPending -> {
            if (isPending) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
        viewModel.getPromptCredentials().observe(this, promptCredentials -> {
            if (promptCredentials) {
                showCredentialsDialog();
            } else {
                hideCredentialsDialog();
            }
        });
    }

    private void showCredentialsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.credentials_dialog_layout);
        mDialog = builder.create();
        mDialog.show();
    }

    private void hideCredentialsDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggleDrawer) {
            if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                closeDrawer();
            } else {
                openDrawer();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onPause() {
        super.onPause();
        hideCredentialsDialog();
    }

    private void setupDrawer(RepoDetailViewModel viewModel) {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mRightDrawer = findViewById(R.id.rightDrawer);
        ListView mRepoOperationList = findViewById(R.id.repoOperationList);

        RepoOperationsAdapter mDrawerAdapter = new RepoOperationsAdapter(this, viewModel);
        mRepoOperationList.setAdapter(mDrawerAdapter);
        mRepoOperationList.setOnItemClickListener(mDrawerAdapter);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mRightDrawer);
    };

    public void openDrawer() {
        mDrawerLayout.openDrawer(mRightDrawer);
    }
}
