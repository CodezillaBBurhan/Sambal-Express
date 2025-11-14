# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose


-ignorewarnings
#-keep class * {
    #  public private *;
#}
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**


# Retrofit
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio
-keep class okio.** { *; }
-dontwarn okio.**
# Refer_Friends
-keepclassmembers class * {
    public <init>();
}
-keep class sambal.mydd.app.models.** { *; }

-keep class retrofit2.** { *; }
-keepattributes Signature
-dontwarn okhttp3.**
-keep class com.google.gson.** { *; }


# Keep JSON models (so Gson/JSONObject can map correctly)
-keep class sambal.mydd.app.models.** { *; }

# Keep field names/annotations for JSON
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Keep Gson annotations
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}

# Keep Retrofit interface methods & annotations
-keepclassmembers,allowobfuscation class * {
    @retrofit2.http.* <methods>;
}

# Keep AsyncCallback (so inner methods aren't stripped)
-keep class sambal.mydd.app.authentication.** { *; }

# Keep your AsyncTask / custom Tasks
-keep class * extends android.os.AsyncTask


# Keep model classes
-keep class sambal.mydd.app.models.** { *; }

# Keep adapter classes
-keep class sambal.mydd.app.adapter.** { *; }

# Keep JSON keys for reflection (if using Gson/JSONObject)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Retrofit response models
-keep class sambal.mydd.app.models.** { *; }

-keep class sambal.mydd.app.utils.** { *; }
-keepclassmembers class * extends android.app.Activity { *; }


# Keep model classes used in JSON
-keep class sambal.mydd.app.model.** { *; }

# Keep adapter classes
-keep class sambal.mydd.app.adapter.** { *; }

# Keep SharedPreferences helpers
-keep class sambal.mydd.app.utils.PreferenceHelper { *; }
-keep class sambal.mydd.app.utils.SharedPreferenceVariable { *; }

# Keep Glide classes
-keep class com.bumptech.glide.** { *; }
-keep class androidx.appcompat.** { *; }

# Keep all annotations
-keepattributes *Annotation*

# Optional: keep all members for View Binding
-keepclassmembers class * extends androidx.viewbinding.ViewBinding {
    <fields>;
}
# Keep model classes
-keep class sambal.mydd.app.beans.MenuList { *; }

# Keep adapter classes
-keep class sambal.mydd.app.adapter.** { *; }

# Keep ViewBinding generated classes
-keepclassmembers class * extends androidx.viewbinding.ViewBinding {
    <fields>;
}

# Keep Glide classes
-keep class com.bumptech.glide.** { *; }

# Keep Preference helpers
-keep class sambal.mydd.app.utils.PreferenceHelper { *; }
-keep class sambal.mydd.app.utils.SharedPreferenceVariable { *; }

# Keep JSON model parsing
-keepattributes Signature
-keepattributes *Annotation*


-keepclassmembers class sambal.mydd.app.beans.MenuList {
    <fields>;
}

# Keep PubNub SDK classes
-keep class com.pubnub.api.** { *; }
-dontwarn com.pubnub.api.**
# PubNub depends on Gson (reflection-heavy)
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
# Ensure annotations survive (for Gson/Retrofit)
-keepattributes Signature, RuntimeVisibleAnnotations, AnnotationDefault, RuntimeVisibleParameterAnnotations
