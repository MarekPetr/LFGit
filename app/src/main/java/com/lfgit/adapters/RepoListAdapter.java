package com.lfgit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lfgit.R;
import com.lfgit.database.Repo;

public class RepoListAdapter extends ArrayAdapter<Repo> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    public RepoListAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
        holder.repoTitle.setText(repo.getDisplayName());
        // TODO delete repo from DB if it doesn't exist

    }


    private class RepoListItemHolder {
        TextView repoTitle;
    }

}
