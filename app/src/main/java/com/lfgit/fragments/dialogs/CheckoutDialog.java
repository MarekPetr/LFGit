package com.lfgit.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.activities.RepoTasksActivity;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

public class CheckoutDialog extends DialogFragment {
    private EditText mBranch;
    private RepoTasksViewModel viewModel;
    private View view;
    private RepoTasksActivity activity;

    public CheckoutDialog() {
        // empty constructor required
    }

    public static CheckoutDialog newInstance() {
        return new CheckoutDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        view = inflater.inflate(R.layout.checkout_layout, null, false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setView(view);

        return alertDialogBuilder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(activity).get(RepoTasksViewModel.class);
        mBranch = view.findViewById(R.id.branchEditText);
        Button mEnterButton = view.findViewById(R.id.enterButton);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String branch = mBranch.getText().toString();
                viewModel.handleCheckoutBranch(branch);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        view = null;
        super.onDestroyView();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        viewModel.startState();
        super.onCancel(dialog);
    }

    @Override
    public void onAttach(Context context) {
        activity = (RepoTasksActivity) context;
        super.onAttach(context);
    }
}
