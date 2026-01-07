import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.coc.zkqcode"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.coc.zkqcode"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }
    buildFeatures {
        compose = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

// 1. 定义一个独立的 Task 来执行部署
tasks.register("deployPatch") {
    // dependsOn("compileDebugKotlin")

    // doLast {
    //     // 使用 serviceOf 获取服务，这是处理 ExecOperations 的官方标准方式
    //     val execOps = project.serviceOf<ExecOperations>()

    //     val patchName = "patch_logic.jar"
    //     val rootDir = project.rootDir
    //     val buildDir = project.layout.buildDirectory.get().asFile
    //     val classDir = File("$buildDir/tmp/kotlin-classes/debug/com/coc/zkqcode/test")
    //     val sdkDir = android.sdkDirectory.path
    //     val buildToolsVersion = android.buildToolsVersion
    //     val d8Path = File(sdkDir, "build-tools/$buildToolsVersion/d8.bat").absolutePath

    //     val classFiles = classDir.listFiles()?.filter {
    //         it.extension == "class" && !it.name.contains("ICalculator")
    //     }?.map { it.absolutePath } ?: emptyList()

    //     if (classFiles.isEmpty()) {
    //         throw GradleException("未发现类文件，请确认代码已保存并执行了 Build -> Make Project")
    //     }

    //     println("🚀 正在转换 ${classFiles.size} 个类文件...")

    //     // 使用 execOps 执行命令
    //     execOps.exec {
    //         commandLine(d8Path)
    //         args("--release", "--output", "$rootDir/$patchName")
    //         args(classFiles)
    //     }

    //     execOps.exec {
    //         commandLine("adb", "push", "$rootDir/$patchName", "/data/local/tmp/mycalculator.jar")
    //     }

    //     println("✅ 部署成功！")
    // }
}

dependencies {

    implementation(libs.libsu.core)
    implementation(libs.androidx.webkit)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}