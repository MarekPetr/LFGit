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
import com.lfgit.activities.RepoDetailActivity;
import com.lfgit.database.RepoDatabase;
import com.lfgit.database.Repo;

import java.util.ArrayList;
import java.util.List;

public class RepoListAdapter extends ArrayAdapter<Repo> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private Context mContext;
    public RepoListAdapter(@NonNull Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(mContext, RepoDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        holder.repoTitle = (TextView) view.findViewById(R.id.repoTitle);
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

    // TODO database
    public void addAllRepos() {
        RepoDatabase repoDb = RepoDatabase.getInstance(mContext);
        Repo repo1 = new Repo("prvni");
        Repo repo2 = new Repo("druhy");

        List<Repo> repos = new ArrayList<>();
        repos.add(repo1);
        repos.add(repo2);
        repoDb.repoDao().insertList(repos);

        clear();
        addAll(repos);
        notifyDataSetChanged();
    }


    private class RepoListItemHolder {
        TextView repoTitle;
    }

}
