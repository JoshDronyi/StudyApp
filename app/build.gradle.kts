plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
}


android {

    compileSdk = libs.versions.compileSDK.get().toInt()
    buildToolsVersion = libs.versions.buildToolsVersion.get()

    defaultConfig {
        with(libs) {
            applicationId = "com.example.studyapp"
            minSdk = versions.minSDK.get().toInt()
            targetSdk = versions.targetSDK.get().toInt()
            versionCode = versions.versionCode.get().toInt()
            versionName = versions.versionName.get()
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )

            }
        }
    }

    buildTypes {
        getByName("release") {
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
        kotlinCompilerExtensionVersion = "1.1.0"
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    //Main Dependencies
    implementation(libs.bundles.main)
    kapt(libs.bundles.compilers)
    //Firebase
    implementation(platform(libs.firebase.platform))
    implementation(libs.bundles.firebase)
    //Testing dependencies.
    testImplementation(libs.bundles.test)
    //end region
}