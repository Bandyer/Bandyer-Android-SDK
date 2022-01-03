# Bandyer Android SDK - (now Kaleyra Video Android SDK)

<p align="center">
<img src="img/kaleyra.png" alt="Kaleyra" title="Kaleyra" />
</p>


 [![Download](https://badgen.net/maven/v/metadata-url/https/maven.bandyer.com/releases/com/bandyer/bandyer-android-sdk/maven-metadata.xml?label=maven.bandyer.com/releases) ](https://maven.bandyer.com/index.html#releases/com/bandyer/bandyer-android-sdk/)[![Docs](https://img.shields.io/badge/docs-current-brightgreen.svg)](https://bandyer.github.io/Bandyer-Android-SDK/)
[![Twitter](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&logo=twitter)](https://twitter.com/intent/follow?screen_name=bandyersrl)


**Kaleyra video** enables audio/video communication and collaboration from any platform and browser! Through its WebRTC architecture, it makes video communication simple and punctual.


---

. **[Overview](#overview)** .
**[Features](#features)** .
**[News](#news-1)** .
**[Documentation](#documentation)** .
**[Pricing](#other)** .
**[Credits](#credits)** .

---

## Overview

**Kaleyra Video Android SDK** makes it easy to add video conference and chat communication to mobile apps.

**Even though this sdk encloses strongly the UI/UX, it is fully styleable through default Android style system.**

<img src="img/img0.png" height="360"/><img src="img/img1.png" height="360"/><img src="img/img2.png" height="360"/><img src="img/img3.png" height="360"/><img src="img/img4.png" height="360"/><img src="img/img5.png" height="360"/><img src="img/img6.png" height="360"/><img src="img/img7.png" height="360"/> 

## Requirements

Supported API level 21+ (Android 5.0 Lollipop).

**Requires compileOptions for Java8**
```java
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## Features

- Audio call
- Audio call upgradable to video call
- Video call
- Chat
- Collaborative whiteboard 
- File sharing in call
- Screen sharing in call:
	- App only
	- System wide
- Call Recording

## Documentation

### Introduction
[Home](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Home)

### Integration
1. [Get your credentials](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Get-Your-Credentials)
1. [Get started](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Get-Started)
1. [Terminology](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Terminology)
1. [Android Studio Setup](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Android-Studio-Setup)
1. [Initialize SDK](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Initialize-BandyerSDK)
1. [Setup BandyerSDKClient](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Initialize-BandyerSDKClient)
    - [Initialize Client](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Initialize-BandyerSDKClient)
    - [Destroy Client](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Initialize-BandyerSDKClient#dispose-bandyersdkclient)
    - [Delete user data](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Initialize-BandyerSDKClient#clear-all-user-data)
    - [Observe Client](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Initialize-BandyerSDKClient#bandyersdkclient-observers)
1. [Start a Call](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Start-a-call)
1. [Observe Call Events](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Observe-call-events)
1. [Start a Chat](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Start-a-chat)
1. [Observe Chat Events](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Observe-Chat-Events)
1. [Handle Push Notifications](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Handle-Push-Notifications)
1. [Handle External Links](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Handle-External-Links)


### Notification Customization
[Call](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-Call-Module-Notifications)

[Chat](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize--Chat-Module-Notifications)

[FileSharing](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-FileSharing-Notifications)

### User Details Display Customization
[User Details Provider](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-user-details#user-details-provider)

[User Details Formatter](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-user-details#user-details-formatter)

### More
[Logging](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Logging)

[Customize Configuration](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-Configuration)

[Call UI Display Mode](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Call-UI-Display-Mode)

[User Identity Verification](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/User-Identity-Verification)

[Unexpected exception handling](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Unexpected-exception-handling)

[Keep BandyerSDKClient Alive](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Keep-client-active-in-background)

[Reduce APK size](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Reduce-the-APK-size)

[Proguard](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Proguard)

### Extensions

[USB Camera](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/USB-Camera)

### Customize UI

🎨  &nbsp; [Color System](https://github.com/Bandyer/Bandyer-Android-Design/wiki/Color-System)

🎑  &nbsp; [Customize Colors & Themes](https://github.com/Bandyer/Bandyer-Android-Design/wiki/Customize-Colors-&-Themes)

🆎  &nbsp; [Customize Font](https://github.com/Bandyer/Bandyer-Android-Design/wiki/Customize-Font)

### Migrations

[Upgrade to v1.5.+](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Upgrade-to-Bandyer-SDK-v1.5.x)

[Upgrade to v2.+](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Upgrade-to-Bandyer-SDK-v2.x)


### Code documentation
[Kotlin](https://docs.bandyer.com/Bandyer-Android-SDK/kDoc/bandyer-android-sdk/)

### Other
[Pricing](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Pricing)

[Troubleshooting](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Troubleshooting)


## Credits
- [WebRTC](https://webrtc.org/) by Google, Mozilla, Opera, W3C and ITF
- [Kotlin](https://github.com/JetBrains/kotlin) by JetBrains
- [Gson](https://github.com/google/gson) by Google
- [OkHttp](https://github.com/square/okhttp) by square
- [Socket.io](https://github.com/socketio/socket.io-client-java) by socket.io
- [Android-weak-handler](https://github.com/badoo/android-weak-handler) by Badoo
- [SiliCompressor](https://github.com/Tourenathan-G5organisation/SiliCompressor) by Tourenathan-G5organisation
- [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher) by permissions-dispatcher
- [Picasso](https://github.com/square/picasso) by square
- [FastAdapter](https://github.com/mikepenz/FastAdapter) by mikepenz
