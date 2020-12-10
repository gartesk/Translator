object Versions {
	const val androidXAppCompat = "1.3.0-alpha02"
	const val androidXCardView = "1.0.0"
	const val androidXNavigation = "2.3.2"
	const val androidXConstraintLayout = "2.1.0-alpha1"
	const val material = "1.3.0-alpha04"
	const val rxJava = "2.2.20"
	const val rxAndroid = "2.1.1"
	const val rxBinding = "3.1.0"
	const val mosbyX = "4.1.0"
	const val retrofit = "2.9.0"
	const val okHttp = "4.9.0"
	const val objectBox = "2.8.1"
	const val kotlin = "1.4.21"
	const val gradle = "4.1.1"
	const val targetSdk = 30
	const val compileSdk = 30
}

object Libs {
	const val androidXAppCompat = "androidx.appcompat:appcompat:${Versions.androidXAppCompat}"
	const val androidXCardView = "androidx.cardview:cardview:${Versions.androidXCardView}"
	const val androidXConstraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.androidXConstraintLayout}"
	const val material = "com.google.android.material:material:${Versions.material}"
	const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
	const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
	const val rxBinding = "com.jakewharton.rxbinding3:rxbinding:${Versions.rxBinding}"
	const val rxBindingMaterial = "com.jakewharton.rxbinding3:rxbinding-material:${Versions.rxBinding}"
	const val mosbyxMvi = "com.github.gartesk:mosbyx:${Versions.mosbyX}"
	const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
	const val androidXNavigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.androidXNavigation}"
	const val androidXNavigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.androidXNavigation}"
	const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
	const val retrofitRxAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
	const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
	const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
	const val objectBoxRx = "io.objectbox:objectbox-rxjava:${Versions.objectBox}"
}

object Plugins {
	const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
	const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
	const val objectBox = "io.objectbox:objectbox-gradle-plugin:${Versions.objectBox}"
}