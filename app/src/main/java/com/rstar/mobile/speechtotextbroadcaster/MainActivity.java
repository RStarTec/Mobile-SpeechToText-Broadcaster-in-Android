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

package com.rstar.mobile.speechtotextbroadcaster;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rstar.mobile.speechtotextbroadcaster.watson.TranscribeFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG=MainActivity.class.getSimpleName() + "_class";

    private Fragment mFragment;
    private int fragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentId = R.id.fragment_container;
        FragmentManager fm = getFragmentManager();

        mFragment = fm.findFragmentById(fragmentId);
        if (mFragment==null) {
            mFragment = TranscribeFragment.newInstance();
            if (mFragment!=null)
                fm.beginTransaction().add(fragmentId, mFragment).commit();
        }
    }

}
