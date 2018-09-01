object Versions {
    const val supportLib = "28+"
    const val rxJava = "2.2.0"
    const val rxAndroid = "2.0.2"
    const val kotlin = "1.2.61"
    const val gradle = "3.1.3"
    const val targetSdk = 28
    const val compileSdk = 28
}

object Libs {
    const val supportAppcompatV7 = "com.android.support:appcompat-v7:${Versions.supportLib}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
}

object Plugins {
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
}