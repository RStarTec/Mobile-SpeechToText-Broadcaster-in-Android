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


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

public class Credentials {
    private static final String PREF_username = Credentials.class.getSimpleName() + "_username";
    private static final String PREF_password = Credentials.class.getSimpleName() + "_password";
    private static final String empty = "";

    private String username = "";
    private String password = "";

    public Credentials(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString(PREF_username, empty);
        password = prefs.getString(PREF_password, empty);
    }

    public void save(Context context, String username, String password) {
        this.username = username;
        this.password = password;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (username==null || username.length()==0) {
            prefs.edit().remove(PREF_username).commit();
        }
        else {
            prefs.edit().putString(PREF_username, username).commit();
        }
        if (password==null || password.length()==0) {
            prefs.edit().remove(PREF_password).commit();
        }
        else {
            prefs.edit().putString(PREF_password, password).commit();
        }
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public JSONObject toJSON() {

        JSONObject json = new JSONObject();
        try {
            json.put("username", (username==null? "" : username) );
            json.put("password", (password==null? "" : password) );
            return json;
        } catch (Exception e) {
            return null;
        }
    }
}
