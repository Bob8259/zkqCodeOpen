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
// Clean old jar class files before recompilation
tasks.register("cleanJarClasses") {
    group = "custom"
    description = "Remove previously built jar class files to avoid stale classes"

//    val workingDir = project.projectDir.absolutePath
//    val possibleClassDirs = listOf(
//        file("$workingDir/build/tmp/kotlin-classes/debug"),
//        file("$workingDir/build/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"),
//        file("$workingDir/build/intermediates/javac/debug/classes")
//    )
//
//    doLast {
//        possibleClassDirs.forEach { dir ->
//            val jarClassDir = File(dir, "com/coc/zkqcode/jar")
//            if (jarClassDir.exists()) {
//                jarClassDir.deleteRecursively()
//                println("Cleaned old classes from: ${jarClassDir.absolutePath}")
//            }
//        }
//    }
}

// Make assembleDebug depend on cleanJarClasses so old classes are removed before compilation
tasks.configureEach {
    if (name == "assembleDebug") {
        dependsOn("cleanJarClasses")
    }
}

// Define a standalone task for deployment
tasks.register<Exec>("deployPatch") {
    group = "custom"
    description = "Compile and package UI plugin into the Assets directory"

    // Ensure the latest class files are compiled before running this task
    dependsOn("assembleDebug")

    // --- Path configuration ---
    val workingDir = project.projectDir.absolutePath
    val sdkDir = System.getenv("ANDROID_HOME") ?: "C:/Users/Azika/AppData/Local/Android/Sdk"

    // Auto-detect the highest installed Build-Tools version (e.g. 36.1.0)
    val buildToolsDir = file("$sdkDir/build-tools")
    val highestBuildTools = buildToolsDir.listFiles()
        ?.filter { it.isDirectory && it.name.contains(".") }
        ?.maxByOrNull { versionFile ->
            versionFile.name.split(".").mapNotNull { it.toIntOrNull() }.let { parts ->
                // Convert version string to a comparable number, e.g. [36, 1, 0]
                parts.fold(0) { acc, i -> acc * 100 + i }
            }
        }

    val d8Path = if (highestBuildTools != null) {
        "${highestBuildTools.absolutePath}/d8.bat"
    } else {
        // Fallback to a known version if auto-detection fails
        "$sdkDir/build-tools/36.1.0/d8.bat"
    }

    // Use the android platform jar as library reference
    val sdkPlatform = "$sdkDir/platforms/android-36/android.jar"
    val outputJar = "$workingDir/src/main/assets/code.jar"
    val flagFile = file("$workingDir/build/tmp/d8_flags.txt")

    // Set the executable
    executable = d8Path

    doFirst {
        // --- Prepare files for conversion ---
        val dependencyFiles = configurations.getByName("debugRuntimeClasspath").files
        
        // Search for class files in all possible output directories
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

        // Clean old output artifact
        val jarFile = file(outputJar)
        if (jarFile.exists()) jarFile.delete()

        if (classFiles.isEmpty()) {
            throw GradleException("No class files found for conversion. Check paths: ${possibleClassDirs.joinToString(", ")}")
        }

        // Build D8 argument list
        val content = mutableListOf<String>()
        content.add("--release")
        content.add("--min-api")
        content.add("24")
        content.add("--lib")
        content.add(sdkPlatform)
        content.add("--output")
        content.add(outputJar)

        // Add dependency libraries (jars only)
        dependencyFiles.forEach { file ->
            if (file.extension == "jar") {
                content.add("--classpath")
                content.add(file.absolutePath)
            }
        }

        // Add our own class files
        classFiles.forEach {
            content.add(it)
        }

        // Write args to a flag file to avoid command-line length and encoding issues
        flagFile.parentFile.mkdirs()
        flagFile.writeText(content.joinToString("\n"), Charsets.UTF_8)

        println("--------------------------------------------------")
        println("Using D8 from: ${highestBuildTools?.name ?: "Default Path"}")
        println("Target Output: $outputJar")
        println("Found ${classFiles.size} class files to convert.")
        println("--------------------------------------------------")
    }

    // Use @ syntax to let d8 read args from the flag file
    args("@${flagFile.absolutePath}")

    doLast {
        if (file(outputJar).exists()) {
            println("--- SUCCESS: Patch deployed to assets/ui.jar ---")
        } else {
            println("--- ERROR: Output file was not generated ---")
        }
    }
}

tasks.register("deployAndReload") {
    group = "custom"
    description = "Build JAR, push to device, and trigger debug reload"
    dependsOn("deployPatch")

    val outputJar = "${project.projectDir.absolutePath}/src/main/assets/code.jar"
    val devicePath = "/data/data/com.coc.zkqcode/files/assets/code.jar"

    doLast {
        // 1. Push JAR to sdcard first (adb push can't write to /data/data directly)
        ProcessBuilder("adb", "push", outputJar, "/sdcard/code.jar")
            .inheritIO().start().waitFor()

        // 2. Copy to private app dir with root
        ProcessBuilder("adb", "shell", "su", "-c",
            "'cp /sdcard/code.jar $devicePath && chmod 644 $devicePath'")
            .inheritIO().start().waitFor()
        println("--- Pushed code.jar to device ---")

        // 3. Send reload broadcast
        ProcessBuilder("adb", "shell", "am", "broadcast",
            "-a", "com.coc.zkqcode.DEBUG_RELOAD",
            "-n", "com.coc.zkqcode/.core.system.daemon.DebugReloadReceiver")
            .inheritIO().start().waitFor()
        println("--- deployAndReload complete ---")
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