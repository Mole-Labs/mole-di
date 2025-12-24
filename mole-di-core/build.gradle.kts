plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kotlin.binary.compatibility)
    id("org.jetbrains.dokka")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("maven") {
                from(components["java"])
                groupId = "com.github.oungsi2000"
                artifactId = "mole-di-core"
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
