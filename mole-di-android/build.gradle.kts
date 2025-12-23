plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("com.diffplug.spotless") version "8.1.0"
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

android {
    namespace = "com.mole.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":mole-di-core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.robolectric)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.oungsi2000"
                artifactId = "mole-di-android"
                version = "1.0.0"

                pom {
                    name.set("Mole-DI")
                    description.set(
                        "Simple & fast android runtime DI framework, " +
                            "Supports lexical scope, with android-friendly Extensions and Scopes",
                    )
                }
            }
        }
    }
}
