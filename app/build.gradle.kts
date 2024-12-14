plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}


android {
    namespace = "com.naseeb.calculatorapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.naseeb.calculatorapp"
        minSdk = 21
        targetSdk = 34
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.rhino)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    implementation(libs.exp4j)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room Database dependencies
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler) // Use annotationProcessor if you're not using Kotlin
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Kotlin annotation processor (kapt)
    kapt(libs.androidx.room.compiler) // This is correct for Kotlin users

    // Room KTX for Kotlin
    implementation(libs.androidx.room.ktx)

    // Coroutines support for Room (if needed)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
