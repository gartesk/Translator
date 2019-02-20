object Versions {
//    const val androidX = "1.0.0"
//    const val material = "1.0.0-rc01"
    const val androidSupport = "28.0.0"
    const val rxJava = "2.2.0"
    const val rxAndroid = "2.0.2"
//    const val rxBinding = "3.0.0-alpha1"
    const val rxBinding = "2.1.1"
    const val mosby = "3.1.0"
    const val jetpackNav = "1.0.0-alpha06"
    const val retrofit = "2.4.0"
    const val okHttp = "3.11.0"
    const val kotlin = "1.2.71"
    const val gradle = "3.1.3"
    const val targetSdk = 28
    const val compileSdk = 28
}

object Libs {
//    const val appcompat = "androidx.appcompat:appcompat:${Versions.androidX}"
//    const val material = "com.google.android.material:material:${Versions.material}"
    const val supportAppcompatV7 = "com.android.support:appcompat-v7:${Versions.androidSupport}"
    const val supportCardViewV7 = "com.android.support:cardview-v7:${Versions.androidSupport}"
    const val supportDesign = "com.android.support:design:${Versions.androidSupport}"
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
//    const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxBinding}"
    const val rxBinding = "com.jakewharton.rxbinding2:rxbinding:${Versions.rxBinding}"
    const val mosbyMvi = "com.hannesdorfmann.mosby3:mvi:${Versions.mosby}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val jetpackNavFragment =
        "android.arch.navigation:navigation-fragment-ktx:${Versions.jetpackNav}"
    const val jetpackNavUI = "android.arch.navigation:navigation-ui-ktx:${Versions.jetpackNav}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitRxAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
}

object Plugins {
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
}