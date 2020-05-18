package com.lfgit.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.activities.RepoTasksActivity;
import com.lfgit.databinding.CheckoutLayoutBinding;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

public class CheckoutDialog extends DialogFragment {
    private RepoTasksViewModel viewModel;
    private CheckoutLayoutBinding mBinding;
    private View view;

    public CheckoutDialog() {
        // empty constructor required
    }

    public static CheckoutDialog newInstance() {
        return new CheckoutDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(requireActivity());
        view = inflater.inflate(R.layout.checkout_layout, null, false);

        CheckoutLayoutBinding binding = CheckoutLayoutBinding.bind(view);
        viewModel = new ViewModelProvider(requireActivity()).get(RepoTasksViewModel.class);
        binding.setRepoTasksViewModel(viewModel);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setView(binding.getRoot());

        return alertDialogBuilder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
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
}