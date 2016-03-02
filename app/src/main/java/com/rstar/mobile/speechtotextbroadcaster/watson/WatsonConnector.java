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

// Adapted from IBM com.ibm.watson.developer_cloud.android.speech_to_text.v1.audio.WebSocketUploader


package com.rstar.mobile.speechtotextbroadcaster.watson;


import com.ibm.watson.developer_cloud.android.speech_to_text.v1.ISpeechDelegate;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.audio.AudioCaptureThread;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.audio.IAudioConsumer;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.audio.IChunkUploader;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.audio.ISpeechEncoder;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.audio.RawEnc;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.dto.SpeechConfiguration;
import com.rstar.mobile.speechtotextbroadcaster.AppSettings;
import com.rstar.mobile.speechtotextbroadcaster.utils.Savelog;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WatsonConnector implements IChunkUploader, IAudioConsumer {
    private static final String TAG = WatsonConnector.class.getName();
    private static final boolean debug = AppSettings.defaultDebug;

    private Socket socket;
    private final Result result = new Result();
    private boolean isRecorder = false;
    private boolean isPrepared = false;
    private ISpeechEncoder encoder = null;
    private AudioCaptureThread audioCaptureThread = null;


    public WatsonConnector(String url, final boolean isRecorder, final Frontend frontend,
                           final JSONObject credentials) throws URISyntaxException {
        this.isRecorder = isRecorder;

        this.socket = IO.socket(url);
        socket.on(io.socket.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (isRecorder) {
                    socket.emit("recorder", credentials);
                    // Do not setPrepared until we receive the recorder status from server
                }
                else {
                    setPrepared(true);
                }
                frontend.showConnection(true);
            }
        });
        socket.on("recorderstatus", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String data = (String) args[0];

                if (isRecorder) {
                    if (data.equals("succeeded")) {
                        setPrepared(true);
                        startRecording();
                    }
                    else {
                        setPrepared(false);
                    }
                }
                frontend.showStatus(data);
            }
        });

        socket.on("server", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String data = (String) args[0];

                frontend.showStatus(data);
            }
        });
        socket.on("interim", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    final JSONObject data = (JSONObject) args[0];
                    frontend.showData(result.parse(data));
                } catch (Exception e) {
                    frontend.showStatus("Received unknown package");
                }
            }
        });
        socket.on("onerror", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String error = (String) args[0];
                frontend.showStatus(error);
            }
        });
        socket.on(io.socket.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                frontend.showConnection(false);
                if (isRecorder) {
                    stopRecording();
                }
            }
        });


        if (isRecorder) {
            encoder = new RawEnc();
        }
    }




    public void setPrepared(boolean value) {
        this.isPrepared = value;
    }



    // IChunkUploader method
    // also shared by receiver
    @Override
    public void prepare() {
        this.isPrepared = false;
        if (isRecorder) {
            try {
                this.encoder.initEncoderWithUploader(this);
            } catch (Exception e) {
                Savelog.e(TAG, "cannot initialize encoder.", e);
            }
        }
        socket.connect();
    }


    // IChunkUploader method
    @Override
    public boolean isUploadPrepared() {
        return this.isPrepared;
    }

    // IChunkUploader method
    @Override
    public int onHasData(byte[] buffer) {
        int uploadedAudioSize = 0;
        // NOW, WE HAVE STATUS OF UPLOAD PREPARING, UPLOAD PREPARING OK
        if (this.isUploadPrepared()) {
            try {
                uploadedAudioSize = encoder.encodeAndWrite(buffer);
                Savelog.d(TAG, debug, "onHasData: " + uploadedAudioSize + " " + buffer.length);
            } catch (IOException e) {
                Savelog.e(TAG, "onHasData error", e);
            }
        }
        else {
        }
        return uploadedAudioSize;
    }

    // IChunkUploader method
    @Override
    public void upload(byte[] data) {
        try{
            // Use audio as a label
            socket.emit("audio", data);
        }
        catch(NotYetConnectedException e){
            Savelog.e(TAG, "upload error", e);
        }
    }

    // IChunkUploader method
    @Override
    public void stop() {
        if (isRecorder) {
            byte[] stopData = new byte[0];
            this.upload(stopData);
        }
    }

    // IChunkUploader method
    @Override
    public void setDelegate(ISpeechDelegate delegate) {
    }

    // IChunkUploader method
    // also shared by receiver
    @Override
    public void close() {
        socket.close();
    }

    // IAudioConsumer method
    @Override
    public void consume(byte[] data) {
        onHasData(data);
    }

    // IAudioConsumer method
    @Override
    public void onAmplitude(double amplitude, double volume) {
    }


    private void startRecording() {
        audioCaptureThread = new AudioCaptureThread(SpeechConfiguration.SAMPLE_RATE, this);
        audioCaptureThread.start();
    }

    private void stopRecording(){
        if(audioCaptureThread != null)
            audioCaptureThread.end();
    }

    public interface Frontend {
        public abstract void showData(String data);
        public abstract void showStatus(String message);
        public abstract void showConnection(boolean connected);
    }
}
