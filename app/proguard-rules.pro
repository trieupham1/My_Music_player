# Keep Activities, Services, and Fragments to avoid runtime issues
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.app.Fragment
-keep class * extends androidx.fragment.app.Fragment

# Keep Firebase model classes (useful for serialization/deserialization)
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}

# Keep methods and classes annotated with @Keep to prevent them from being removed or obfuscated
-keep @androidx.annotation.Keep class * {*;}
-keepclassmembers class * {
    @androidx.annotation.Keep <fields>;
}

# Keep Parcelable and Serializable classes
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
}

# Keep Retrofit interfaces to avoid runtime issues during network calls
-keep interface com.example.api.** { *; }

# Keep GSON serialized model classes
-keep class com.example.model.** { *; }

# Keep ViewModel classes for Android Architecture Components
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Strip out log statements for smaller APKs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Avoid warnings for third-party libraries
-dontwarn com.google.**
-dontwarn retrofit2.**
-dontwarn okio.**

# Keep Firebase classes and attributes accessed via reflection
-keepattributes Signature
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
