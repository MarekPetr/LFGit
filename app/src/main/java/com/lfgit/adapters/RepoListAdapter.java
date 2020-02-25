package com.lfgit.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.lfgit.R;
import com.lfgit.activities.BasicAbstractActivity;
import com.lfgit.activities.RepoDetailActivity;
import com.lfgit.database.model.Repo;
import com.lfgit.view_models.RepoListViewModel;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class RepoListAdapter extends ArrayAdapter<Repo> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private BasicAbstractActivity mContext;
    private RepoListViewModel mViewModel;

    public RepoListAdapter(@NonNull BasicAbstractActivity context, RepoListViewModel viewModel) {
        super(context, 0);
        mContext = context;
        mViewModel = viewModel;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(mContext, RepoDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        BasicAbstractActivity.onOptionClicked[] dialog = new BasicAbstractActivity.onOptionClicked[]{
                () -> {
                    deleteRepo(position);
                },
        };

        mContext.showOptionsDialog(
                R.string.dialog_choose_option,
                R.array.repo_options,
                dialog
        );

        return true;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(getContext(), parent);
        }
        bindView(convertView, position);
        return convertView;
    }

    private View newView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.repo_list_item, parent, false);
        RepoListItemHolder holder = new RepoListItemHolder();
        holder.repoTitle = view.findViewById(R.id.repoTitle);
        view.setTag(holder);
        return view;
    }

    private void bindView(View view, int position) {
        RepoListItemHolder holder = (RepoListItemHolder) view.getTag();
        final Repo repo = getItem(position);
        if (repo != null) {
            holder.repoTitle.setText(repo.getDisplayName());
        }
        // TODO delete repo from DB if it doesn't exist
    }

    public void setRepos(List<Repo> repos) {
        clear();
        addAll(repos);
        notifyDataSetChanged();
    }

    private void deleteRepo(int position) {
        final Repo repo = getItem(position);
        assert repo != null;
        remove(repo);
        notifyDataSetChanged();
        mViewModel.deleteRepoById(repo.getId());
    }

    private class RepoListItemHolder {
        TextView repoTitle;
    }
}
