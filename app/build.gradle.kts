plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.8.20-1.0.11" apply true
}

android {

    compileSdk = 34
    namespace = "komoot.challenge"

    defaultConfig {

        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName  = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    sourceSets.named("main") {
        java.srcDir("build/generated/ksp/kotlin")
    }
}

dependencies {

    val composeVersion = "1.5.4"
    val composeNavVersion = "2.7.5"
    val composeAccompanistVersion = "0.30.1"
    val composeConstraintLayoutVersion = "1.0.1"

    val material3Version = "1.1.2"
    val media3Version = "1.1.1"

    val koinVersion = "3.5.0"
    val koinKspVersion = "1.3.0"

    val ktorVersion = "2.3.5"
    val roomVersion = "2.5.2"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("com.google.android.material:material:1.10.0")

    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.foundation:foundation-layout:$composeVersion")
    implementation("androidx.navigation:navigation-compose:$composeNavVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:$composeConstraintLayoutVersion")

    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")

    implementation("com.google.accompanist:accompanist-systemuicontroller:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-pager:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-navigation-material:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-placeholder:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-drawablepainter:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-permissions:$composeAccompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$composeAccompanistVersion")

    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")

    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-android-compat:$koinVersion")
    implementation("io.insert-koin:koin-androidx-workmanager:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("io.insert-koin:koin-annotations:$koinKspVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinKspVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    //noinspection GradleDependency
    implementation("androidx.room:room-runtime:$roomVersion")
    //noinspection GradleDependency
    implementation("androidx.room:room-ktx:$roomVersion")
    //noinspection GradleDependency
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    //noinspection GradleDependency
    ksp("androidx.room:room-compiler:$roomVersion")


    val jUnitVersion = "4.13.2"
    val androidJUnitVersion = "1.1.5"
    val espressoVersion = "3.5.1"

    testImplementation("junit:junit:$jUnitVersion")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")

    androidTestImplementation("androidx.test.ext:junit:$androidJUnitVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")

}
