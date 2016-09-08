package com.spudesc.cfttest.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.spudesc.cfttest.R;

/**
 * Created by Roman Babenko (roman.babenko@sibers.com) on 9/8/2016.
 */
public class DialogFragment extends android.app.DialogFragment {
    private static final String TEXT_KEY = "text_key";

    public static DialogFragment newInstance(String text) {
        DialogFragment f = new DialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(DialogFragment.TEXT_KEY, text);
        f.setArguments(arguments);
        return f;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String text = getArguments().getString(TEXT_KEY);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.title_text))
                .setMessage(text)
                .setCancelable(true)
                .setNegativeButton(getResources().getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }
}
