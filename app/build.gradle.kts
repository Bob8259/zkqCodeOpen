import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.coc.zkqcode"
    compileSdk = 36

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use {
            localProperties.load(it)
        }
    }
    val serverPublicKey: String = localProperties.getProperty("SERVER_PUBLIC_KEY") ?: ""

    defaultConfig {
        applicationId = "com.coc.zkqcode"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SERVER_PUBLIC_KEY", "\"$serverPublicKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug { isDebuggable = true }
    }
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
// 1. 定义一个独立的 Task 来执行部署
tasks.register<Exec>("deployPatch") {
    group = "custom"
    description = "编译并打包 UI 插件到 Assets 目录"

    // 确保在执行此任务前先编译出最新的 class 文件
    dependsOn("assembleDebug")

    // --- 路径配置 ---
    val workingDir = project.projectDir.absolutePath
    val sdkDir = System.getenv("ANDROID_HOME") ?: "C:/Users/Azik/AppData/Local/Android/Sdk"

    // 自动寻找本机安装的最高版本 Build-Tools (例如 36.1.0)
    val buildToolsDir = file("$sdkDir/build-tools")
    val highestBuildTools = buildToolsDir.listFiles()
        ?.filter { it.isDirectory && it.name.contains(".") }
        ?.maxByOrNull { versionFile ->
            versionFile.name.split(".").mapNotNull { it.toIntOrNull() }.let { parts ->
                // 将版本号转为数字列表进行比较，如 [36, 1, 0]
                parts.fold(0) { acc, i -> acc * 100 + i }
            }
        }

    val d8Path = if (highestBuildTools != null) {
        "${highestBuildTools.absolutePath}/d8.bat"
    } else {
        // 如果没找到，尝试指向你确有的版本作为兜底
        "$sdkDir/build-tools/36.1.0/d8.bat"
    }

    // 建议使用 android-34 或 35 的 jar 作为类库参考
    val sdkPlatform = "$sdkDir/platforms/android-36/android.jar"
    val outputJar = "$workingDir/src/main/assets/code.jar"
    val flagFile = file("$workingDir/build/tmp/d8_flags.txt")

    // 设置执行的程序
    executable = d8Path

    doFirst {
        // --- 准备待转换的文件 ---
        val dependencyFiles = configurations.getByName("debugRuntimeClasspath").files
        
        // 尝试从多个可能的路径查找 class 文件
        val possibleClassDirs = listOf(
            file("$workingDir/build/tmp/kotlin-classes/debug"),
            file("$workingDir/build/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"),
            file("$workingDir/build/intermediates/javac/debug/classes")
        )

        val classFiles = mutableListOf<String>()
        possibleClassDirs.forEach { dir ->
            if (dir.exists()) {
                val files = fileTree(dir) {
                    include("com/coc/zkqcode/jar/**/*.class")
                }.files.map { it.absolutePath }
                classFiles.addAll(files)
                println("Found ${files.size} class files in ${dir.absolutePath}")
            }
        }

        // 清理旧产物
        val jarFile = file(outputJar)
        if (jarFile.exists()) jarFile.delete()

        if (classFiles.isEmpty()) {
            throw GradleException("未找到待转换的 class 文件，请检查路径: ${possibleClassDirs.joinToString(", ")}")
        }

        // 构建 D8 参数列表
        val content = mutableListOf<String>()
        content.add("--release")
        content.add("--min-api")
        content.add("24")
        content.add("--lib")
        content.add(sdkPlatform)
        content.add("--output")
        content.add(outputJar)

        // 添加依赖库 (只包含 jar)
        dependencyFiles.forEach { file ->
            if (file.extension == "jar") {
                content.add("--classpath")
                content.add(file.absolutePath)
            }
        }

        // 添加我们自己的类文件
        classFiles.forEach {
            content.add(it)
        }

        // 写入参数文件，解决命令行过长和编码问题
        flagFile.parentFile.mkdirs()
        flagFile.writeText(content.joinToString("\n"), Charsets.UTF_8)

        println("--------------------------------------------------")
        println("Using D8 from: ${highestBuildTools?.name ?: "Default Path"}")
        println("Target Output: $outputJar")
        println("Found ${classFiles.size} class files to convert.")
        println("--------------------------------------------------")
    }

    // 使用 @ 符号让 d8 读取参数文件
    args("@${flagFile.absolutePath}")

    doLast {
        if (file(outputJar).exists()) {
            println("--- SUCCESS: Patch deployed to assets/ui.jar ---")
        } else {
            println("--- ERROR: Output file was not generated ---")
        }
    }
}

tasks.register<Exec>("rustBuild") {
    group = "build"
    description = "Build Rust logic using cargo-ndk"
    workingDir = file("../rust_logic")
    commandLine(
        "cargo",
        "ndk",
        "-t",
        "arm64-v8a",
        "-t",
        "armeabi-v7a",
        "-t",
        "x86",
        "-t",
        "x86_64",
        "-o",
        "../app/src/main/jniLibs",
        "build",
        "--release"
    )
}

dependencies {

    implementation(libs.libsu.core)
    implementation(libs.androidx.webkit)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.mlkit.text.recognition.chinese)
    implementation(libs.mlkit.text.recognition)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    debugImplementation(libs.leakcanary.android)
}