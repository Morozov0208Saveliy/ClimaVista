plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android") version "2.48"
}

android {
    namespace = "com.example.climavista"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.climavista"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.example.climavista.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_KEY", "\"${findProperty("OPENWEATHER_API_KEY")}\"")
        }
        debug {
            buildConfigField("String", "API_KEY", "\"${findProperty("OPENWEATHER_API_KEY")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // AndroidX Activity and ConstraintLayout
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // BlurView
    implementation(libs.blurview)

    // Glide for image loading
    implementation(libs.glide)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.work.runtime.ktx)
    kapt(libs.hilt.compiler)

    // Hilt testing dependencies
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)

    // Retrofit for networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)

    // OkHttp for networking
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Coroutines for asynchronous tasks
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // WeatherView for displaying weather effects
    implementation(libs.weatherview)

    testImplementation(libs.androidx.core.testing)

    // Fragment Testing - для работы с launchFragmentInContainer
    debugImplementation(libs.androidx.fragment.testing.v151)

    // Espresso - для UI тестирования
    androidTestImplementation(libs.androidx.espresso.core.v340)
    androidTestImplementation (libs.androidx.espresso.contrib.v340)

    // JUnit - для тестов
    androidTestImplementation(libs.androidx.junit.v113)

    // Mockito - для мокирования зависимостей
    testImplementation(libs.mockito.core.v3112)
    androidTestImplementation(libs.mockito.android)
    testImplementation(libs.mockito.inline.v3112)

    // Coroutines Test - для тестирования корутин
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation ("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest:2.2")

    androidTestImplementation ("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
}

kapt {
    correctErrorTypes = true
}