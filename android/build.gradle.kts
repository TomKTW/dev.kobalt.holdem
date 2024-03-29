plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "dev.kobalt.holdem.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.kobalt.holdem.android"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            resValue("string", "app_version", "2022.05.01")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            resValue("string", "app_version", "2022.05.01")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android JDK Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    // Android SVG Support
    implementation("com.caverock:androidsvg-aar:1.4")
    // AndroidX AppCompat
    implementation("androidx.appcompat:appcompat:1.4.1")
    // AndroidX Core Kotlin Extensions
    implementation("androidx.core:core-ktx:1.7.0")
    // AndroidX Fragment Kotlin Extensions
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    // AndroidX Lifecycle Extensions
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    // AndroidX Lifecycle Java 8 Common
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.1")
    // AndroidX Lifecycle Runtime Kotlin Extensions
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    // AndroidX Lifecycle LiveData Kotlin Extensions
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    // AndroidX Lifecycle ViewModel Kotlin Extensions
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    // AndroidX Preferences Kotlin Extensions
    implementation("androidx.preference:preference-ktx:1.2.0")
    // Google Material Design
    implementation("com.google.android.material:material:1.5.0")
    // Ktor HTTP Logging
    implementation("io.ktor:ktor-client-logging:1.6.1")
    // Ktor HTTP Client OkHttp
    implementation("io.ktor:ktor-client-okhttp:1.6.1")
    // Ktor HTTP Client Websockets
    implementation("io.ktor:ktor-client-websockets:1.6.1")
    // KotlinX Coroutines Core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    // KotlinX Coroutines Android
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    // KotlinX Serialization Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    // Simple Logging Facade
    implementation("org.slf4j:slf4j-android:1.7.32")
    // Simple Stack Navigation
    implementation("com.github.Zhuinden:simple-stack:2.6.2")
    // Simple Stack Navigation Extensions
    implementation("com.github.Zhuinden:simple-stack-extensions:2.2.2")
    // JUnit
    testImplementation("junit:junit:4.13.2")
    // AndroidX JUnit
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    // AndroidX Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}