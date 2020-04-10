package com.lfgit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lfgit.R;
import com.lfgit.activities.RepoTasksActivity;
import com.lfgit.adapters.RepoTasksAdapter.DrawerItem;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

// source:
// https://github.com/maks/MGit/blob/master/app/src/main/java/me/sheimi/sgit/adapters/RepoOperationsAdapter.java
public class RepoTasksAdapter extends ArrayAdapter<DrawerItem>
        implements OnItemClickListener {

    private RepoTasksViewModel mViewModel;
    private RepoTasksActivity mRepoTasksActivity;

    public RepoTasksAdapter(Context context, RepoTasksViewModel viewModel) {
        super(context, 0);
        setupDrawerItem();
        mViewModel = viewModel;
        mRepoTasksActivity = (RepoTasksActivity) context;
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
        View view = inflater.inflate(R.layout.drawer_list_item, parent, false);
        DrawerItemHolder holder = new DrawerItemHolder();
        holder.name = view.findViewById(R.id.name);
        view.setTag(holder);
        return view;
    }

    private void bindView(View view, int position) {
        DrawerItemHolder holder = (DrawerItemHolder) view.getTag();
        DrawerItem item = getItem(position);
        assert item != null;
        holder.name.setText(item.name);
    }

    public static class DrawerItemHolder {
        public TextView name;
    }

    public static class DrawerItem {
        public String name;
        DrawerItem(String name) {
            this.name = name;
        }
    }

    private void setupDrawerItem() {
        String[] ops = getContext().getResources().getStringArray(
                R.array.repo_operation_names);
        for (String op : ops) {
            add(new DrawerItem(op));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id)
    {
        mRepoTasksActivity.closeDrawer();
        mViewModel.execGitTask(position);
    }
}
