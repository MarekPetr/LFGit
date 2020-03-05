package com.lfgit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lfgit.R;
import com.lfgit.adapters.RepoOperationsAdapter.DrawerItem;
import com.lfgit.view_models.RepoDetailViewModel;

import org.jetbrains.annotations.NotNull;

// source:
// https://github.com/maks/MGit/blob/master/app/src/main/java/me/sheimi/sgit/adapters/RepoOperationsAdapter.java
public class RepoOperationsAdapter extends ArrayAdapter<DrawerItem>
        implements OnItemClickListener {

    private RepoDetailViewModel mViewModel;

    public RepoOperationsAdapter(Context context, RepoDetailViewModel viewModel) {
        super(context, 0);
        setupDrawerItem();
        mViewModel = viewModel;
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
                            long id) {
        if (position == 0) {
            mViewModel.gitAddAllToStage();
        }
        switch(position) {
            case(0): mViewModel.gitAddAllToStage(); break;
            case(1): mViewModel.gitCommit();        break;
            case(2): mViewModel.gitPush();          break;
            case(3): mViewModel.gitPull();          break;
            case(4): mViewModel.gitNewBranch();     break;
            case(5): mViewModel.gitAddRemote();     break;
            case(6): mViewModel.gitRemoveRemote();  break;
            case(7): mViewModel.gitMerge();         break;
        }
    }
}
