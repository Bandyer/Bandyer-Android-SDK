# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep ic_bandyer_options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.


# Bandyer proprietary SDK
-keep class com.bandyer.** { *; }
-keep interface com.bandyer.** { *; }
-keep enum com.bandyer.** { *; }

# WebRTC library used for Audio&Video communication
-keep class org.webrtc.** { *; }
-keep interface org.webrtc.** { *; }
-keep enum org.webrtc.** { *; }

# Twilio library used for Chat communication
-keep class com.twilio.** { *; }
-keep interface com.twilio.** { *; }
-keep enum com.twilio.** { *; }

# Pushy
-dontwarn me.pushy.**
-keep class me.pushy.** { *; }
