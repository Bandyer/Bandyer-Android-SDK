# Bandyer Android SDK

<p align="center">
<img src="img/bandyer.jpg" alt="Bandyer" title="Bandyer" />
</p>


[ ![Download](https://api.bintray.com/packages/bandyer/Communication/Bandyer-Android-SDK/images/download.svg) ](https://bintray.com/bandyer/Communication/Bandyer-Android-SDK/_latestVersion)[![Docs](https://img.shields.io/badge/docs-current-brightgreen.svg)](https://bandyer.github.io/Bandyer-Android-SDK/)
[![Twitter](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&logo=twitter)](https://twitter.com/intent/follow?screen_name=bandyersrl)


**Bandyer** is a young innovative startup that enables audio/video communication and collaboration from any platform and browser! Through its WebRTC architecture, it makes video communication simple and punctual.


---

. **[Overview](#overview)** .
**[Features](#features)** .
**[News](#news)** .
**[Documentation](#documentation)** .
**[Support](#support)** .
**[Credits](#credits)** .

---

## Overview

**Bandyer Android SDK** makes it easy to add video conference and chat communication to mobile apps.

**Even though this sdk encloses strongly the UI/UX, it is fully stylable through default Android style system.**

<img src="img/chat.png" height="360"/><img src="img/audio_call.png" height="360"/><img src="img/video_call.png" height="360"/> <img src="img/pip.png" height="360"/> 

## Requirements

Bandyer Android SDK is supported from API level 16 (Android 4.1 Jelly Bean).

**Bandyer Android SDK requires compileOptions for Java8**
```java
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## Features

- Audio&Video calls
- Audio calls
- Audio calls upgradable to Video Calls
- Chat
- Collaborative whiteboard 
- FileSharing in Call
- Calls Recording

## News
2019-05-16: Bandyer-Android-SDK 1.1.15 has been released. This release increases performance on some devices and to do so requires JAVA_8. Other change read in [release notes](https://github.com/Bandyer/Bandyer-Android-SDK/releases/tag/v1.1.15).

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
1. [Create a Chat](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Create-a-chat)
1. [Create a Call](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Create-a-call)
1. [Handle Push Notifications](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Handle-Push-Notifications)
1. [Handle External Links](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Handle-External-Links)


### Notification Customization
[Call](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-Call-Module-Notifications)

[Chat](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize--Chat-Module-Notifications)

[FileSharing](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Customize-FileSharing-Notifications)

### User Details Display Customization
[User Contact Provider](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/User-contact-provider)

[User Details Formatter](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/User-details-formatter)

### More
[Logging](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Logging)

[Keep BandyerSDKClient Alive](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Keep-client-active-in-background)

[Proguard](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Proguard)

### Customize UI
> Coming soon 

### Migrations
[ Upgrade to v1.1.x](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Upgrade-to-BandyerSDK-1.1.x)

### Code documentation
[Kotlin](https://bandyer.github.io/Bandyer-Android-SDK/kDoc/)

[Java](https://bandyer.github.io/Bandyer-Android-SDK/jDoc/)

### Other
[Pricing](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Pricing)

[Troubleshooting](https://github.com/Bandyer/Bandyer-Android-SDK/wiki/Troubleshooting)

## Credits
- [WebRTC](https://webrtc.org/) by Google, Mozilla, Opera, W3C and ITF
- [Gson](https://github.com/google/gson) by Google
- [Android-weak-handler](https://github.com/badoo/android-weak-handler) by Badoo
- [Socket.io](https://github.com/socketio/socket.io-client-java) by socket.io
