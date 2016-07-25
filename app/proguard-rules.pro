# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/john/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class com.jnhyxx.html5.AppJs {
   public *;
}
-keepattributes *JavascriptInterface*
# or
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}

# Remove log code
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# Fixed: SDK
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}

## Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

## Attributes
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses

# Jar: via sdk v1
-keep class com.wo.**{*;}
-keep public class org.eclipse.paho.**{*;}

# Picasso
-dontwarn com.squareup.okhttp.**

# This is the content of 'proguard-android-optimize.txt':
#
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-optimizationpasses 5
#-allowaccessmodification
#-dontpreverify
#
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose
#
#-keepattributes *Annotation*
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService
#
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-keepclassmembers public class * extends android.view.View {
#   void set*(***);
#   *** get*();
#}
#
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keepclassmembers class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator CREATOR;
#}
#
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
#-dontwarn android.support.**
#
#-keep class android.support.annotation.Keep
#
#-keep @android.support.annotation.Keep class * {*;}
#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <methods>;
#}
#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <fields>;
#}
#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <init>(...);
#}
