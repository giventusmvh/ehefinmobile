# ============================================
# EheFin Mobile - ProGuard Security Rules
# ============================================

# Optimization settings for maximum obfuscation
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# ============================================
# Preserve line info for crash reports (optional - remove for maximum obfuscation)
# ============================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# Android Framework Classes (REQUIRED)
# ============================================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View

# ============================================
# Kotlin & Coroutines
# ============================================
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ============================================
# Hilt Dependency Injection
# ============================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# ============================================
# Retrofit & OkHttp Networking
# ============================================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers,allowobfuscation class * {
    @retrofit2.http.* <methods>;
}

# Keep API interfaces
-keep,allowobfuscation interface com.example.ehefin_mobile.**.api.*Api

# ============================================
# Gson Serialization
# ============================================
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep data classes (DTOs) for serialization
-keep class com.example.ehefin_mobile.**.data.model.** { *; }
-keep class com.example.ehefin_mobile.**.data.dto.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# Room Database
# ============================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ============================================
# Firebase
# ============================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keepattributes *Annotation*

# ============================================
# Compose UI
# ============================================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============================================
# DataStore Preferences
# ============================================
-keep class androidx.datastore.** { *; }

# ============================================
# Coil Image Loading
# ============================================
-keep class coil.** { *; }
-dontwarn coil.**

# ============================================
# WorkManager
# ============================================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ============================================
# Enum classes
# ============================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# Parcelable
# ============================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ============================================
# R8 Full Mode
# ============================================
-allowaccessmodification
-repackageclasses ''

# ============================================
# Security - Additional obfuscation
# ============================================
# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep security classes
-keep class com.example.ehefin_mobile.core.security.** { *; }

# Obfuscate class names aggressively
-flattenpackagehierarchy