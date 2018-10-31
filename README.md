# Bandyer Android SDK

<p align="center">
<img src="img/bandyer.jpg" alt="Bandyer" title="Bandyer" />
</p>


[ ![Download](https://api.bintray.com/packages/bandyer/Communication/Bandyer-Android-SDK/images/download.svg) ](https://bintray.com/bandyer/Communication/Bandyer-Android-SDK/_latestVersion)[![Docs](https://img.shields.io/badge/docs-current-brightgreen.svg)](https://bandyer.github.io/Bandyer-Android-SDK/)
[![Twitter](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&logo=twitter)](https://twitter.com/intent/follow?screen_name=bandyersrl)


Bandyer is a young innovative startup that enables audio/video communication and collaboration from any platform and browser! Through its WebRTC architecture, it makes video communication simple and punctual.


---

. **[Overview](#overview)** .
**[Features](#features)** .
**[Installation](#installation)** .
**[Quickstart](#quickstart)** .
**[Documentation](#documentation)** .
**[Support](#support)** .
**[Credits](#credits)** .

---

## Overview

Bandyer Android SDK allows you to integrate with low effort video and chat communication in your app.

**Even though this sdk encloses strongly the UI/UX, it is fully stylable through default Android style system.**

<img src="img/chat.png" height="360"/><img src="img/audio_call.png" height="360"/><img src="img/video_call.png" height="360"/> <img src="img/pip.png" height="360"/> 

## Features

- Audio&Video calls
- Audio calls
- Audio calls upgradable to Video Calls
- Chat

## Installation

Download the [latest AAR](https://bintray.com/bandyer/Communication/Bandyer-Android-SDK) or grab via Gradle:

```groovy
implementation 'com.bandyer:bandyer-android-sdk:1.0.1'
```

## Quickstart

Initialize Bandyer SDK Module in your Application class.

```java
BandyerSDK.Builder builder = new BandyerSDK.Builder(
			getApplicationContext(),
 			"Your APP_ID",
 			"Your API_KEY");

 builder.withChatEnabled()   // enable chat feature
	.withCallEnabled()  // enable audio/video call feature
	.setEnvironment(Environment.Configuration.sandbox()); // or Environment.Configuration.production()

BandyerSDK.init(builder);

```

## Initialize Bandyer SDK Client

```java
BandyerSDKClientOptions options = new BandyerSDKClientOptions.Builder().build();

BandyerSDKClient.getInstance().init(this.getApplicationContext(), userAlias, MainActivity.this, options);

BandyerSDKClient.getInstance().startListening(); // Start listening for incoming call
BandyerSDKClient.getInstance().stopListening(); // Stop listening for incoming call
```

Click here for the [options](#custom-options)

## Dispose Bandyer SDK Client

```java
BandyerSDKClient.getInstance().dispose();
```

## Start an audio/video call with chat:

```java
BandyerIntent.Builder.CallIntentBuilder builder = new BandyerIntent.Builder(MainActivity.this)
        .startWithCall()
        .withChatCapability()
        .withUserAliases(new ArrayList<>(calleeSelected))
        // Optional user display formatter to be used during a call
        .withUserDisplayFormatter(new UserDisplayInfoFormatter() { 
            @NonNull
            @Override
            public String format(@NonNull UserDisplayInfo userDisplayInfo) {
                return userDisplayInfo.getNickName() + " " + userDisplayInfo.getEmail();
            }
        })
        .audioVideoCall()
        // .audioCall()
        // .audioUpgradableCall()
        .recordCall(); // optional call recording

Intent callIntent = builder.build();
startActivityForResult(callIntent, START_CALL_CODE); // start call
```

## Start a one-to-one chat:

```java
BandyerIntent.Builder.ChatIntentBuilder builder = new BandyerIntent.Builder(MainActivity.this)
        .startWithChat()
        //  .withAudioVideoCallCapability()
        // or
        //  .withAudioCallCapability(upgradable = false)
        .withUserAlias(userAlias)
        .withUserDisplayFormatter(new UserDisplayInfoFormatter() {
            @NonNull
            @Override
            public String format(@NonNull UserDisplayInfo userDisplayInfo) {
                return userDisplayInfo.getNickName() + " " + userDisplayInfo.getEmail();
            }
        });

Intent chatIntent = builder.build();
startActivityForResult(chatIntent, START_CHAT_CODE); // start chat
```

## Custom options

```java

BandyerSDKClientOptions.Builder builder = new BandyerSDKClientOptions.Builder();

// Provide a *UserInformationFetcher* if you want to display differently the user details
builder.withUserInformationFetcher(new UserInformationFetcher() {
    @Override
    public void fetchUser(@NonNull final String userAlias,
                          @NonNull final OnUserInformationFetcherObserver fetcherObserver) {

        UserDisplayInfo detailsInfo = new UserDisplayInfo.Builder(userAlias)
                .withFirstName("firstname")
                .withLastName("lastname")
                .withNickName("nickname")
                .withEmail("email")
                .build();

        fetcherObserver.onUserFetched(detailsInfo);
    }

    @Override
    public void fetchUserImage(final @NonNull String userAlias,
                               final @NonNull OnUserImageFetcherObserver fetcherObserver) {

        UserImageDisplayInfo imageInfo = new UserImageDisplayInfo.Builder(userAlias)
                .withImageUrl("https://www.google.com/image.png");
        //      or
        //      .withImageBitmap(bitmap)
        //      or
        //      .withResId(resId)
        fetcherObserver.onUserImagesFetched(imageInfo);
    }
});

// Provide a NotificationConfig with/without formatter to customize notifications
builder.withNotificationConfig(new BandyerSDKNotificationConfig.Builder()
        .setNotificationSmallIcon(R.drawable.ic_bandyer_notification)
        .setNotificationColor(R.drawable.bandyer_selected_item_color)
        .setIncomingCallSmallIcon(R.drawable.ic_bandyer_audio_call)
        .withNotificationDisplayFormatter(new UserDisplayInfoFormatter() {
        @NonNull
        @Override
        public String format(@NonNull UserDisplayInfo displayInfo) {
            return displayInfo.getNickName() + " " + displayInfo.getEmail();
        }
}).build());

// Keep the connection always on  **use wisely** mainly for testing purposes!
// Remember that the OS may kill your app at anytime while the app is in background
builder.keepListeningforIncomingCallsInBackground(false);

BandyerSDKClientOptions options = builder.build();
```
## Documentation
You can find the complete documentation in two different styles

Kotlin Doc: [https://bandyer.github.io/Bandyer-Android-SDK/kDoc/](https://bandyer.github.io/Bandyer-Android-SDK/kDoc/)

Java Doc: [https://bandyer.github.io/Bandyer-Android-SDK/jDoc/](https://bandyer.github.io/Bandyer-Android-SDK/jDoc/)

## Customize style
- Documentation coming soon

## Pricing
Contact us at <mailto:info@bandyer.com> to get your appID and apiKey if you don't have one yet!

## Support
To get basic support please submit an [Issue](https://github.com/Bandyer/Bandyer-Android-SDK/issues)

If you prefer commercial support, please contact [bandyer.com](https://bandyer.com) by mail: <mailto:info@bandyer.com>.


## Credits
- [WebRTC](https://webrtc.org/) by Google, Mozilla, Opera, W3C and ITF
- [Gson](https://github.com/google/gson) by Google
- [Android-weak-handler](https://github.com/badoo/android-weak-handler) by Badoo
- [Socket.io](https://github.com/socketio/socket.io-client-java) by socket.io


