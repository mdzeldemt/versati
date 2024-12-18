buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.android.gradle)
    }
}

plugins {
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
