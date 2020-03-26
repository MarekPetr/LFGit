package com.lfgit.fragments;

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

public class RemoteDialog extends DialogFragment {
    private AddRemoteDialogListener mListener;
    private EditText mRemoteURL;

    public RemoteDialog() {
        // empty constructor required
    }

    public static RemoteDialog newInstance(AddRemoteDialogListener listener) {
        RemoteDialog dialog = new RemoteDialog();
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_remote_layout, null, false);

        mRemoteURL = view.findViewById(R.id.remotePathEditText);
        Button mEnterButton = view.findViewById(R.id.enterButton);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setView(view);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remoteURL = mRemoteURL.getText().toString();
                mListener.handleRemoteURL(remoteURL);
            }
        });
        return alertDialogBuilder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onCancelAddRemoteDialog();
    }

    public interface AddRemoteDialogListener {
        void handleRemoteURL(String remoteURL);
        void onCancelAddRemoteDialog();
    }
}
