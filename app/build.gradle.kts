plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.myswimsmartdb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myswimsmartdb"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packagingOptions {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
            merges += setOf("META-INF/native-image/reflect-config.json", "META-INF/native-image/resource-config.json")
        }
    }
}

dependencies {
    implementation ("nl.dionsegijn:konfetti-compose:2.0.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.compose.runtime:runtime-saveable:1.7.0-beta04")
    implementation ("androidx.compose.foundation:foundation:1.7.0-beta04")
    implementation ("androidx.compose.material:material:1.7.0-beta04")
    implementation ("androidx.compose.ui:ui:1.7.0-beta03")
    implementation ("androidx.compose.ui:ui-tooling:1.7.0-beta03")
    implementation ("androidx.compose.runtime:runtime:1.7.0-beta04")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0-RC")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.34.0")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.34.0")
    implementation ("androidx.compose.ui:ui:1.7.0-beta03")
    implementation ("androidx.compose.material3:material3:1.3.0-beta04")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.7.0-beta03")
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.0-beta04")
    implementation ("androidx.compose.runtime:runtime-rxjava2:1.7.0-beta04")
    implementation ("androidx.compose.foundation:foundation:1.6.8")
    implementation ("androidx.compose.ui:ui:1.6.8")
    implementation ("androidx.compose.ui:ui-tooling:1.6.8")
    implementation ("androidx.compose.material:material:1.6.8")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.compose.runtime:runtime:1.6.8")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.0-beta04")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.7.0-beta04")
    implementation ("com.itextpdf:itext7-core:8.0.4")

    // Other dependencies
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0-RC")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.34.0")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.34.0")

    // Testing dependencies
    testImplementation( "junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.2.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:1.7.0-beta03")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
