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


# 1. 保护你自己的所有代码
-keep class com.coc.zkqcode.interfaces.** { *; }
-keep class com.coc.zkqcode.core.** { *; }
-keep class com.coc.zkqcode.nativehelper.** { *; }
-keep class com.coc.zkqcode.statehelper.** { *; }
-keep class com.coc.zkqcode.MainActivity { *; }

# 2. 核心：保护所有 JAR 可能用到的第三方库名号
# 既然你的项目依赖了这么多，JAR 很可能也用了它们
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class com.google.gson.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class timber.log.** { *; }
-keep class com.topjohnwu.superuser.** { *; }

# 3. 针对 Kotlin 运行时的保护（重要）
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# 4. 基础属性保持
-keepattributes Exceptions,Signature,InnerClasses,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# 3. 单独给 loadjar 开个口子：允许混淆它
# 注意：在 ProGuard 中，如果有两条规则冲突，Keep 优先。
# 但我们可以通过“混淆配置”让 R8 尝试处理它。
# 事实上，如果你已经 -keep 了整个包，loadjar 也会被保护。