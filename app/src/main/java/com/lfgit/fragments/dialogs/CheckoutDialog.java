package com.lfgit.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.lfgit.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CheckoutDialog extends DialogFragment {
    private CheckoutDialogListener mListener;
    private EditText mBranch;

    public CheckoutDialog() {
        // empty constructor required
    }

    public static CheckoutDialog newInstance(CheckoutDialogListener listener) {
        CheckoutDialog dialog = new CheckoutDialog();
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.checkout_layout, null, false);

        mBranch = view.findViewById(R.id.branchEditText);
        Button mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String branch = mBranch.getText().toString();
                mListener.handleCheckoutBranch(branch);
            }
        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onCancelCheckoutDialog();
    }

    public interface CheckoutDialogListener {
        void handleCheckoutBranch(String branch);
        void onCancelCheckoutDialog();
    }
}
