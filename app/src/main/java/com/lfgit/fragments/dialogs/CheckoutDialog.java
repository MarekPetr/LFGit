package com.lfgit.fragments.dialogs;
import com.lfgit.R;


public class CheckoutDialog extends EnterTextDialog {

    public CheckoutDialog() {
        // empty constructor required
    }

    public static CheckoutDialog newInstance() {
        return new CheckoutDialog();
    }

    @Override
    void handleText(String text) {
        viewModel.handleCheckoutBranch(text);
    }

    @Override
    int getDialogLayoutID() {
        return R.layout.checkout_dialog;
    }
}