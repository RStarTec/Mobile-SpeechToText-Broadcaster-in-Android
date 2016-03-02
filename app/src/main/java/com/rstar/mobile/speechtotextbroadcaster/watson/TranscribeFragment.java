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

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rstar.mobile.speechtotextbroadcaster.AppSettings;
import com.rstar.mobile.speechtotextbroadcaster.R;
import com.rstar.mobile.speechtotextbroadcaster.utils.Savelog;

public class TranscribeFragment extends Fragment implements WatsonConnector.Frontend {
    private static final String TAG = TranscribeFragment.class.getSimpleName() + "_class";
    private static final boolean debug = AppSettings.defaultDebug;

    private enum Role { recorder, receiver };
    private enum State { idle, connected };

    private int labelRecord = R.string.label_startRecording;
    private int labelReceive = R.string.label_startReceiving;
    private int labelStop = R.string.label_stop;

    private Role mRole = Role.receiver;
    private State mState = State.idle;

    private WatsonConnector connector = null;
    private String mStatus = "";
    private String mData = "";

    private ToggleButton mRoleToggle;
    private TextView mStatusView;
    private Button mStateButton;
    private TextView mDataView;



    public static TranscribeFragment newInstance() {
        Bundle args = new Bundle();
        TranscribeFragment fragment = new TranscribeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Savelog.d(TAG, debug, "onCreate()");

        // Make sure to retain the fragment so that initialization is done once
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transcribe, parent, false);

        mRoleToggle = (ToggleButton) v.findViewById(R.id.fragmentTranscribe_toggleRole);
        mStatusView = (TextView) v.findViewById(R.id.fragmentTranscribe_status);
        mStateButton = (Button) v.findViewById(R.id.fragmentTranscribe_buttonMain);
        mDataView = (TextView) v.findViewById(R.id.fragmentTranscribe_data);

        mStatusView.setText(mStatus);
        mDataView.setText(mData);

        mRoleToggle.setChecked(mRole == Role.recorder);
        mRoleToggle.setOnClickListener(new RoleListener());

        mStateButton.setOnClickListener(new StateListener(this));

        showStateButton();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                FragmentManager fm = getFragmentManager();
                CredentialsDialogFragment dialog = CredentialsDialogFragment.newInstance();
                dialog.show(fm, CredentialsDialogFragment.dialogTag);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        if (connector!=null) {
            connector.stop();
            connector.close();
        }
        super.onDestroy();
    }


    private class RoleListener implements ToggleButton.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mRoleToggle.isChecked()) {
                mRole = Role.recorder;
            }
            else {
                mRole = Role.receiver;
            }
            showStateButton();
        }
    }

    private class StateListener implements View.OnClickListener {
        WatsonConnector.Frontend hostFragment;
        public StateListener(TranscribeFragment hostFragment) {
            this.hostFragment = hostFragment;
        }
        @Override
        public void onClick(View view) {
            if (mState == State.idle) {
                // Once the button is clicked the role is fixed until the session is over
                try {
                    Credentials credentials = new Credentials(getActivity());
                    Savelog.d(TAG, debug, "Credentials:" + (credentials.toJSON()!=null ? credentials.toJSON().toString() : "(null)"));
                    connector = new WatsonConnector(AppSettings.host, (mRole==Role.recorder), hostFragment, credentials.toJSON());
                    connector.prepare();
                    mRoleToggle.setClickable(false);
                } catch (Exception e) {
                    Savelog.e(TAG, "unable to initiate connector", e);
                    connector = null;
                    mState = State.idle;
                    mRoleToggle.setClickable(true);
                    showStateButton();
                }
            }
            else if (mState == State.connected) {
                mRoleToggle.setClickable(true);
                connector.stop();
                connector.close();
                mState = State.idle;
                showStateButton();
            }
        }
    }

    public void showStateButton() {
        if (mState == State.idle) {
            if (mRole == Role.recorder) {
                mStateButton.setText(getString(labelRecord));
            } else {
                mStateButton.setText(getString(labelReceive));
            }
        }
        else {
            mStateButton.setText(getString(labelStop));
        }
    }

    // Frontend method
    @Override
    public void showConnection(final boolean connected) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    mState = State.connected;
                }
                else {
                    mState = State.idle;
                }
                showStateButton();
            }
        });
    }

    // Frontend method
    @Override
    public void showStatus(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus = message;
                mStatusView.setText(message);
            }
        });
    }


    // Frontend method
    @Override
    public void showData(final String data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mData = data;
                mDataView.setText(data);
            }
        });
    }

}