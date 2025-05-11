# Keep all model classes in your library
-keep class com.lib.pay.from.libpfu.models.** { *; }

# Keep annotations used by Gson
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Needed for Gson reflection to access fields
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson-specific internal (optional)
-keep class sun.misc.Unsafe { *; }

# Keep all constructors
-keepclassmembers class * {
    public <init>(...);
}
