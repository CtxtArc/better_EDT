# Keep model and data classes for Gson serialization
-keep class com.example.better_edt.data.** { *; }
-keep class com.example.better_edt.model.** { *; }
-keep class com.example.better_edt.network.** { *; }

# Keep OkHttp & Gson
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.google.gson.**
