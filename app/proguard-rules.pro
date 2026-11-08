# Reglas de Proguard / R8 para TRACE

# Mantener modelos de dominio y serializables
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# kotlinx.serialization
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**
