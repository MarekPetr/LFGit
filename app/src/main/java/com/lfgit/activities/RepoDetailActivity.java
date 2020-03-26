package com.lfgit.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.adapters.RepoOperationsAdapter;
import com.lfgit.database.model.Repo;
import com.lfgit.databinding.ActivityRepoDetailBinding;
import com.lfgit.fragments.CredentialsDialog;
import com.lfgit.view_models.RepoDetailViewModel;


public class RepoDetailActivity extends BasicAbstractActivity {
    private RelativeLayout mRightDrawer;
    private DrawerLayout mDrawerLayout;
    private ActivityRepoDetailBinding mBinding;
    private CredentialsDialog mDialogFragment;
    private RepoDetailViewModel mRepoDetailViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_repo_detail);
        mRepoDetailViewModel = new ViewModelProvider(this).get(RepoDetailViewModel.class);
        mBinding.setRepoDetailViewModel(mRepoDetailViewModel);
        mBinding.setLifecycleOwner(this);

        setupDrawer();
        mBinding.taskResult.setMovementMethod(new ScrollingMovementMethod());

        Repo repo = (Repo) getIntent().getSerializableExtra(Repo.TAG);
        mRepoDetailViewModel.setRepo(repo);

        mRepoDetailViewModel.getExecPending().observe(this, isPending -> {
            if (isPending) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
        mRepoDetailViewModel.getPromptCredentials().observe(this, promptCredentials -> {
            if (promptCredentials) {
                showCredentialsDialog();
            } else {
                hideCredentialsDialog();
            }
        });

        mRepoDetailViewModel.getShowToast().observe(this, message -> {
            showToastMsg(message);
        });
    }

    private void showCredentialsDialog() {
        mDialogFragment = CredentialsDialog.newInstance(mBinding.getRepoDetailViewModel());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        mDialogFragment.show(ft, "dialog");
    }

    private void hideCredentialsDialog() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
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

    private void setupDrawer() {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mRightDrawer = findViewById(R.id.rightDrawer);
        ListView mRepoOperationList = findViewById(R.id.repoOperationList);

        RepoOperationsAdapter mDrawerAdapter = new RepoOperationsAdapter(this, mRepoDetailViewModel);
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
