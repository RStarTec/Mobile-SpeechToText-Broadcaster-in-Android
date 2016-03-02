/*
 * Copyright (c) 2015,2016 Annie Hui @ RStar Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rstar.mobile.speechtotextbroadcaster.watson;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rstar.mobile.speechtotextbroadcaster.AppSettings;
import com.rstar.mobile.speechtotextbroadcaster.R;
import com.rstar.mobile.speechtotextbroadcaster.utils.Savelog;


public class CredentialsDialogFragment extends DialogFragment {
    private static final String TAG = CredentialsDialogFragment.class.getSimpleName()+"_class";
    private static final boolean debug = AppSettings.defaultDebug;

    public static final String dialogTag = CredentialsDialogFragment.class.getSimpleName()+"_tag";

    private enum Fields { username, password};
    private String mUsername;
    private String mPassword;
    private EditText mUsernameView;
    private EditText mPasswordView;

    public static CredentialsDialogFragment newInstance() {
        Bundle args = new Bundle();

        CredentialsDialogFragment fragment = new CredentialsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Savelog.d(TAG, debug, "onCreate() entered");

        Credentials credentials = new Credentials(getActivity());
        mUsername = credentials.getUsername();
        mPassword = credentials.getPassword();

        setRetainInstance(true);
        Savelog.d(TAG, debug, "This dialog fragment is retained.");
    }


    /* This dialog has a title, a TextView and one button (OK).
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_credentials, null);

        mUsernameView = (EditText) v.findViewById(R.id.dialogCredentials_username);
        mPasswordView = (EditText) v.findViewById(R.id.dialogCredentials_password);
        mUsernameView.setText(mUsername);
        mPasswordView.setText(mPassword);
        // put cursor at the end
        mUsernameView.setSelection(mUsernameView.getText().length());
        mPasswordView.setSelection(mPasswordView.getText().length());

        mUsernameView.addTextChangedListener(new FieldTextWatcher(Fields.username));
        mPasswordView.addTextChangedListener(new FieldTextWatcher(Fields.password));

        // If in testing phase, allow user to reset the password
        Button button = (Button) v.findViewById(R.id.dialogCredentials_reset);
        if (AppSettings.testerEnabled) {
            button.setText("Reset");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mUsernameView!=null) mUsernameView.setText(AppSettings.username);
                    if (mPasswordView!=null) mPasswordView.setText(AppSettings.password);
                }
            });
        }
        else {
            button.setVisibility(View.GONE);
        }

        /* Use the Builder class for convenient dialog construction.
         * The dialog builder just needs to handle OK.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Credentials credentials = new Credentials(getActivity());
                        credentials.save(getActivity(), mUsername, mPassword);
                    }
                })
                .setNeutralButton(R.string.button_cancel, null)
                .setNegativeButton(R.string.button_erase, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Credentials credentials = new Credentials(getActivity());
                        credentials.save(getActivity(), null, null);
                    }
                });
        Dialog dialog = builder.create();
        return dialog;

    } // end to onCreateDialog()


    @Override
    public void onDestroyView() {
        /* As of Aug 2013, Dialog Fragment has a bug with its
         * SetRetainedInstance() method. Therefore, the following
         * need to be done to retain the dialog fragment
         */
        if (getDialog()!=null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }


    private class FieldTextWatcher implements TextWatcher {
        Fields field;
        public FieldTextWatcher(Fields field) {
            super();
            this.field = field;
        }

        @Override
        public void afterTextChanged(Editable arg0) {}
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {
            if (c!=null && c.toString().trim().length()>0) {
                if (field==Fields.username)
                    mUsername = c.toString().trim();
                else
                    mPassword = c.toString().trim();
            }
            else {
                if (field==Fields.username)
                    mUsername = "";
                else
                    mPassword = "";
            }
        }
    }

}
