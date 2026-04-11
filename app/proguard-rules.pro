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

-assumenosideeffects class java.io.PrintStream {
    public void println(java.lang.String);
    public void print(java.lang.String);
}


# ==========================================================
# 1. Keep all app source code
# ==========================================================
-keep class com.coc.zkqcode.interfaces.** { *; }
-keep class com.coc.zkqcode.core.** { *; }
-keep class com.coc.zkqcode.nativehelper.** { *; }
-keep class com.coc.zkqcode.statehelper.** { *; }
-keep class com.coc.zkqcode.MainActivity { *; }

# ==========================================================
# 2. Google ML Kit rules (fix missing class errors)
# ==========================================================
# Keep all ML Kit text recognition classes and members
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.mlkit.common.** { *; }

# Keep Google API and GMS internal calls (required by ML Kit for model loading)
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }

# Keep common ML Kit vision classes (required for chunked model loading)
-keep class com.google.mlkit.vision.common.** { *; }

# ==========================================================
# 3. Third-party library keep rules
# ==========================================================
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class com.google.gson.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class timber.log.** { *; }
-keep class com.topjohnwu.superuser.** { *; }

# ==========================================================
# 4. Kotlin runtime and coroutines (required to avoid Dispatcher errors)
# ==========================================================
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, Exceptions

# ==========================================================
# 5. TensorFlow Lite rules
# ==========================================================

# Keep all TFLite core classes and members (prevents JNI call failures)
-keep class org.tensorflow.lite.** { *; }

# Keep TFLite Support library (Interpreter, TensorBuffer, ImageProcessor, etc.)
-keep class org.tensorflow.lite.support.** { *; }

# Keep native methods, as TFLite relies heavily on C++ native implementations
-keepclasseswithmembernames class * {
    native <methods>;
}

# Prevent metadata parsing errors (for models that include Metadata)
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keep class org.tensorflow.lite.annotations.** { *; }

# ==========================================================
# 6. Suppress warnings for JVM-only classes referenced transitively by dependencies
# ==========================================================
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
