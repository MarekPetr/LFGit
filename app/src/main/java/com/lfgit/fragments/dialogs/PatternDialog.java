package com.lfgit.fragments.dialogs;

import com.lfgit.R;

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
        return R.layout.pattern_dialog;
    }
}
