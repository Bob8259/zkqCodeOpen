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
# 1. 保护你自己的所有代码 (保持现状)
# ==========================================================
-keep class com.coc.zkqcode.interfaces.** { *; }
-keep class com.coc.zkqcode.core.** { *; }
-keep class com.coc.zkqcode.nativehelper.** { *; }
-keep class com.coc.zkqcode.statehelper.** { *; }
-keep class com.coc.zkqcode.MainActivity { *; }

# ==========================================================
# 2. Google ML Kit 专用规则 (解决找不到类的问题)
# ==========================================================
# 保持 ML Kit 文本识别的所有类及其成员
-keep class com.google.mlkit.vision.text.** { *; }
-keep class com.google.mlkit.common.** { *; }

# 保持 Google API 和 GMS 相关内部调用 (ML Kit 依赖这些进行模型加载)
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }

# 如果你使用了分块加载模型，建议加上这个
-keep class com.google.mlkit.vision.common.** { *; }

# ==========================================================
# 3. 基础第三方库保护
# ==========================================================
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class com.google.gson.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class timber.log.** { *; }
-keep class com.topjohnwu.superuser.** { *; }

# ==========================================================
# 4. Kotlin 运行时与协程 (关键，因为你的报错涉及 Dispatcher)
# ==========================================================
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, Exceptions

# ==========================================================
# 5. TensorFlow Lite 专用规则
# ==========================================================

# 保持 TFLite 核心库的所有类及其成员 (防止 JNI 调用失败)
-keep class org.tensorflow.lite.** { *; }

# 保持 TFLite Support 库 (如果你使用了 Interpreter, TensorBuffer, ImageProcessor 等)
-keep class org.tensorflow.lite.support.** { *; }

# 特别保护 Native 方法，因为 TFLite 严重依赖 C++ 底层实现
-keepclasseswithmembernames class * {
    native <methods>;
}

# 预防元数据解析错误 (如果你的模型包含 Metadata)
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keep class org.tensorflow.lite.annotations.** { *; }