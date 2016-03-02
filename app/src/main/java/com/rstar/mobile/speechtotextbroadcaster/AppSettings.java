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

public class AppSettings {
    private static final String TAG = AppSettings.class.getSimpleName()+"_class";

    public static final boolean defaultDebug = false;  // Generate debug messages if set
    public static final boolean testerEnabled = false; // Save the log file in external folder if set, allow tester to report bugs to developer
    public static final String defaultFolder = "/SpeechToTextBroadcaster";


    // TODO: Customize the following:

    // Host is the bluemix app that provides the socket for receiving data, transcribing and broadcasting results
    public static final String host = "http://STTBroadcast.mybluemix.net/";

    // The default credentials which will be available during the testing phase
    public static final String username = "(username-of-your-service)";
    public static final String password = "(password-of-your-service)";
}
