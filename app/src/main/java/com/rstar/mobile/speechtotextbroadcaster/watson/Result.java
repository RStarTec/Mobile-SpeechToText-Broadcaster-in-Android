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


import com.rstar.mobile.speechtotextbroadcaster.AppSettings;
import com.rstar.mobile.speechtotextbroadcaster.utils.Savelog;

import org.json.JSONArray;
import org.json.JSONObject;

public class Result {
    private static final String TAG = Result.class.getName();
    private static final boolean debug = AppSettings.defaultDebug;
    private static final int MaxLength = 300;  // Maximum number of characters allowed to be displayed.

    String finalizedData = "";


    public Result() {
    }

    public String parse(JSONObject json) {
        String result = finalizedData;

        try {
            if (json.has("results")) {
                //if has result
                Savelog.d(TAG, debug, "Results message: ");
                JSONArray results = json.getJSONArray("results");
                for (int i=0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);
                    JSONArray alternatives = obj.getJSONArray("alternatives");
                    String transcript = alternatives.getJSONObject(0).getString("transcript");

                    String intermediate = Character.toUpperCase(transcript.charAt(0)) + transcript.substring(1);
                    if (obj.getString("final").equals("true")) {
                        String fullstop = ". ";

                        // Once the total length of text is beyond limit, reset
                        if (finalizedData.length() + intermediate.length() > MaxLength) {
                            finalizedData = intermediate.substring(0, intermediate.length() - 1) + fullstop;
                        }
                        else { // no need to reset. Just add data
                            finalizedData += intermediate.substring(0, intermediate.length() - 1) + fullstop;
                        }

                        result = finalizedData;
                    } else {
                        result = finalizedData + intermediate;

                    }
                    break;
                }
            }
        } catch (Exception e) {
            Savelog.w(TAG, "Unrecognized json object");
            result = "(unrecognized)";
        }
        return result;
    }
}
