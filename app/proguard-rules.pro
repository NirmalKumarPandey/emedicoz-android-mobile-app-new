# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Cbc-03\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes Signature

# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep public class org.jsoup.** {
public *;
}

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep class com.emedicoz.app.Model.offlineData { *; }
#-keep class com.emedicoz.app.utils.offlinedata.StoreProvider { *; }
#-dontobfuscate // enable this will support data migration for 3.9.28
-keep class com.emedicoz.app.utils.OfflineData.StoreProvider { *; }

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


-keep, allowobfuscation class com.emedicoz.*

-keepclassmembers, allowobfuscation class * {
*;
}

-dontwarn org.mortbay.**
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-keep class okio.** { *; }
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8



# PallyCon
-keep class com.pallycon.** {*;}
-keep class com.google.android.exoplayer2.** {*;}
-keep class net.sqlcipher.** {*; }
-keep class com.whitecryption.skb.** {*;}


-keep class javax.annotation.** { *; }
-dontwarn javax.annotation.**

-keep class javax.xml.** { *; }
-dontwarn javax.xml.**

-dontwarn com.bumptech.**

-keep class com.emedicoz.app.Model**
-keepclassmembers class com.emedicoz.app.Model** {
*;
}

-keep class com.emedicoz.app.model**
-keepclassmembers class com.emedicoz.app.model** {
*;
}

-keep class com.emedicoz.app.bookmark.model**
-keepclassmembers class com.emedicoz.app.bookmark.model** {
*;
}

-keep class com.emedicoz.app.combocourse.model**
-keepclassmembers class com.emedicoz.app.combocourse.model** {
*;
}

-keep class com.emedicoz.app.courses.model**
-keepclassmembers class com.emedicoz.app.courses.model** {
*;
}

-keep class com.emedicoz.app.feeds.model**
-keepclassmembers class com.emedicoz.app.feeds.model** {
*;
}

-keep class com.emedicoz.app.flashcard.model**
-keepclassmembers class com.emedicoz.app.flashcard.model** {
*;
}

-keep class com.emedicoz.app.installment.model**
-keepclassmembers class com.emedicoz.app.installment.model** {
*;
}

-keep class com.emedicoz.app.pastpaperexplanation.model**
-keepclassmembers class com.emedicoz.app.pastpaperexplanation.model** {
*;
}

-keep class com.emedicoz.app.referralcode.model**
-keepclassmembers class com.emedicoz.app.referralcode.model** {
*;
}

-keep class com.emedicoz.app.recordedCourses.model**
-keepclassmembers class com.emedicoz.app.recordedCourses.model** {
*;
}

-keep class com.emedicoz.app.retrofit.models**
-keepclassmembers class com.emedicoz.app.retrofit.models** {
*;
}

-keep class com.emedicoz.app.testmodule.model**
-keepclassmembers class com.emedicoz.app.testmodule.model** {
*;
}

-keep class com.emedicoz.app.video.model**
-keepclassmembers class com.emedicoz.app.video.model** {
*;
}

-keep class com.emedicoz.app.polls.model**
-keepclassmembers class com.emedicoz.app.polls.model** {
*;
}
-keep class com.emedicoz.app.podcast**
-keepclassmembers class com.emedicoz.app.podcast** {
*;
}


-keep class com.emedicoz.app.response**
-keepclassmembers class com.emedicoz.app.response** {
*;
}
-keep class com.emedicoz.app.podcast.PhoneCallReceiver
-keepclassmembers class com.emedicoz.app.podcast.ServiceReceiver** {
*;
}

-keep class com.emedicoz.app.network.model**
-keepclassmembers class com.emedicoz.app.network.model** {
*;
}

-keepnames class com.emedicoz.app.feeds.fragment.RegistrationFragment
-keepnames class com.emedicoz.app.feeds.fragment.SpecializationFragment
-keepnames class com.emedicoz.app.feeds.fragment.SubStreamFragment
-keepnames class com.emedicoz.app.feeds.fragment.CourseInterestedInFragment
-keepnames class com.emedicoz.app.retrofit.ApiClient
#-keepnames class com.paytm.pgsdk.PaytmPGActivity

# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keep class org.jsoup.**
-keep class com.shockwave.**
-keep class com.epubear.** { *; }

-keep class com.emedicoz.app.network.model**
-keepclassmembers class com.emedicoz.app.network.model** {
*;
}

-keep class com.emedicoz.app.newsandarticle.models**
-keepclassmembers class com.emedicoz.app.newsandarticle.models** {
*;
}

-keep class com.emedicoz.app.rating**
-keepclassmembers class com.emedicoz.app.rating** {
*;
}

-keep class com.emedicoz.app.video.ui.models**
-keepclassmembers class com.emedicoz.app.video.ui.models** {
*;
}