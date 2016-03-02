
### Purpose

The aim is to build a <i>live-transcription broadcasting service</i> for use in settings 
of public gathering. In such settings, the speech recorded from the speaker is transcribed 
and broadcasted to members of the audience in nearly real time. People with special hearing 
or language needs may benefit from the visual aid provided by this service in such environments 
as lectures and conferences.

### Context

The <i>live-transcription broadcasting service</i> has three components: the <b>Server</b>, the <b>Clients</b> and the <b>Backbone</b>. 

- The <b>Backbone</b> is the Watson Speech-To-Text (STT) service which is responsible for performing 
the live transcription.

- The <b>Server</b> resides on Bluemix. It acts as the mediator between the clients and the STT service. 
Audio received from the client (in the recorder role) is passed on to the STT service. When 
transcription result is available from the STT service, the server broadcasts the result to all 
clients.

- The <b>Clients</b> may come from the web (web client), a mobile device (mobile client), or the Google 
Glass (Google Glass client). This project builds a <b><i>mobile client</i></b> component.

### Design of the Mobile Client

Mobile clients may play the role of either a recorder or a receiver. A recorder has the exclusive
privilege of recording speech for transcription. Receivers are only allowed to receive the 
transcription results.

Only one client may be the recorder at any time. Once a client has successfully acquired the 
recorder role from the server, recording begins and audio data is streamed in to the server. The 
server will broadcast the transcription result to all clients. The mobile client relinquishes its 
recorder role once it stops recording. Other eligible clients may request for this role when it 
is available.

The mobile client connects to the server through socket.io.


### Source code

- Server: https://hub.jazz.net/project/wrstar/SpeechToText-broadcast/overview
- Android client: https://github.com/RStarTec/SpeechToTextBroadcaster-Android
- Google Glass client: https://github.com/RStarTec/SpeechToTextReceiver-GoogleGlass

### TODO

2. Current support for mobile clients is limited to Android devices. An iOS client may be developed.

### Terms of Use

This project is under the Apache2 license. 
The author(s) intended this project for beneficial uses in society and would appreciate this 
wish to be respected.

### Contact

Please contact us for any feedback or suggestions. Thank you!
