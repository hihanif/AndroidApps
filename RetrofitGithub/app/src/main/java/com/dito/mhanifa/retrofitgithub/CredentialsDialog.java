package com.dito.mhanifa.retrofitgithub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;

public class CredentialsDialog extends DialogFragment {

    public interface ICredentialsDialogCallback {
        public void onDialogPositiveClick(String username, String password);
    }

    ICredentialsDialogCallback listener;

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ICredentialsDialogCallback) {
            listener = (ICredentialsDialogCallback) getActivity();
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_credentials, null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Enter your credentials:")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(
                            ((EditText)view.findViewById(R.id.editText_username)).getText().toString(),
                            ((EditText)view.findViewById(R.id.editText_password)).getText().toString());
                    }
                }).create();

        return dialog;
    }
}
