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