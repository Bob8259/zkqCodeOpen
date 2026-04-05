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
    val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""

    defaultConfig {
        applicationId = "com.coc.zkqcode"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
tasks.register<Exec>("buildJar") {
    group = "build"
    description = "Compile and package UI plugin into the Assets directory"

    // Ensure the latest class files are compiled before running this task
    dependsOn("assembleDebug")

    // --- Path configuration ---
    val workingDir = project.projectDir.absolutePath
    val sdkDir = "C:/Users/Azikaban/AppData/Local/Android/Sdk"

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
    // Use current timestamp (seconds since epoch minus offset) as the jar name
    val timestamp = System.currentTimeMillis() / 1000 - 1770000000
    val outputJar = "$workingDir/src/main/assets/$timestamp.jar"
    val flagFile = file("$workingDir/build/tmp/d8_flags.txt")

    // Set the executable
    executable = d8Path

    doFirst {
        // Delete all old .jar files from the assets directory before building
        val assetsPath = file("$workingDir/src/main/assets")
        assetsPath.listFiles()?.filter { it.extension == "jar" }?.forEach {
            it.delete()
            println("Deleted old jar: ${it.name}")
        }

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
        val outFile = file(outputJar)
        if (outFile.exists()) {
            println("--- SUCCESS: JAR built at ${outFile.name} ---")

            // Encrypt the JAR using conda base Python
            val pythonExe = "C:/Users/Azikaban/anaconda3/python.exe"
            val scriptPath = file("encrypt_jar.py").absolutePath
            val proc = ProcessBuilder(pythonExe, scriptPath, outFile.absolutePath)
                .inheritIO().start()
            val exitCode = proc.waitFor()
            if (exitCode != 0) {
                throw GradleException("JAR encryption failed with exit code $exitCode")
            }
        } else {
            println("--- ERROR: Output file was not generated ---")
        }
    }
}

tasks.register("validateUploadJarGuard") {
    group = "verification"
    description = "Block uploadJar when MainScript test code is still enabled"

    doLast {
        val mainScriptFile = file("src/main/java/com/coc/zkqcode/jar/code/MainScript.kt")
        if (!mainScriptFile.exists()) {
            throw GradleException("Cannot validate upload safety because MainScript.kt was not found.")
        }

        val scriptLines = mainScriptFile.readLines()
        val runMainScriptStart = scriptLines.indexOfFirst { it.contains("suspend fun runMainScript()") }
        val runTestCodeDeclaration = scriptLines.indexOfFirst { it.contains("private suspend fun runTestCode()") }
        if (runMainScriptStart == -1 || runTestCodeDeclaration == -1 || runTestCodeDeclaration <= runMainScriptStart) {
            throw GradleException("Cannot validate upload safety because the expected MainScript structure was not found.")
        }

        // Only scan the main script body so the helper declaration itself does not trigger the guard.
        val runMainScriptLines = scriptLines.subList(runMainScriptStart, runTestCodeDeclaration)
        val activeRunTestCodeLine = Regex("""^\s*runTestCode\s*\(\s*\)\s*(?://.*)?$""")
        val isTestHookEnabled = runMainScriptLines.any { line ->
            activeRunTestCodeLine.matches(line)
        }

        if (isTestHookEnabled) {
            throw GradleException(
                "runTestCode() is still enabled in MainScript.kt. Comment out or disable that call before running uploadJar."
            )
        }
    }
}

tasks.named("buildJar") {
    mustRunAfter("validateUploadJarGuard")
}

// Upload the encrypted JAR to the hot update server
tasks.register("uploadJar") {
    group = "build"
    description = "Build, encrypt, and upload the JAR to the hot update server"
    dependsOn("validateUploadJarGuard")
    dependsOn("buildJar")

    doLast {
        val assetsPath = file("${project.projectDir.absolutePath}/src/main/assets")
        val encryptedJar = assetsPath.listFiles()
            ?.firstOrNull { it.name.startsWith("encrypted_") && it.extension == "jar" }
            ?: throw GradleException("No encrypted jar found in assets directory")

        val pythonExe = "C:/Users/Azikaban/anaconda3/python.exe"
        val scriptPath = file("upload_jar.py").absolutePath

        println("Uploading ${encryptedJar.name} to hot update server...")
        val proc = ProcessBuilder(pythonExe, scriptPath, encryptedJar.absolutePath)
            .inheritIO().start()
        val exitCode = proc.waitFor()
        if (exitCode != 0) {
            throw GradleException("JAR upload failed with exit code $exitCode")
        }
        println("--- Upload complete ---")
    }
}

tasks.register("deployAndReload") {
    group = "custom"
    description = "Build JAR, push to device, and trigger debug reload"
    dependsOn("buildJar")

    doLast {
        // Dynamically find the built jar in assets directory
        val assetsPath = file("${project.projectDir.absolutePath}/src/main/assets")
        val jarFile = assetsPath.listFiles()?.firstOrNull { it.extension == "jar" }
            ?: throw GradleException("No jar found in assets directory")
        val devicePath = "/data/data/com.coc.zkqcode/files/assets/${jarFile.name}"

        // 1. Remove stale jar files from /sdcard before pushing
        ProcessBuilder("adb", "shell", "rm", "-f", "/sdcard/*.jar")
            .inheritIO().start().waitFor()

        // 2. Push JAR to sdcard first (adb push can't write to /data/data directly)
        ProcessBuilder("adb", "push", jarFile.absolutePath, "/sdcard/${jarFile.name}")
            .inheritIO().start().waitFor()

        // 3. Copy to private app dir with root
        ProcessBuilder("adb", "shell", "su", "-c",
            "'cp /sdcard/${jarFile.name} $devicePath && chmod 644 $devicePath'")
            .inheritIO().start().waitFor()
        println("--- Pushed ${jarFile.name} to device ---")

        // 4. Send reload broadcast
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