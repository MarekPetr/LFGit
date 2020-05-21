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
import androidx.lifecycle.ViewModelProvider;

import com.lfgit.R;
import com.lfgit.view_models.RepoTasksViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PatternDialog extends EnterTextDialog {
    public PatternDialog() {
        // empty constructor required
    }

    public static PatternDialog newInstance() {
        return new PatternDialog();
    }

    @Override
    void handleText(String text) {
        viewModel.handlePattern(text);
    }

    @Override
    int getDialogLayoutID() {
        return R.layout.pattern_dialog_layout;
    }
}
