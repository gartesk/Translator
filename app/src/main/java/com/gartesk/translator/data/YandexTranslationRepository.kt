package com.gartesk.translator.data

import com.gartesk.translator.BuildConfig
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Maybe
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class YandexTranslationRepository : TranslationRepository {

    private val apiUrl = "https://translate.yandex.net/api/v1.5/tr.json/"
    private val apiKey = BuildConfig.YANDEX_API_KEY

    private val api = Retrofit.Builder()
        .baseUrl(apiUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(YandexApi::class.java)

    override fun translate(text: String): Maybe<String> =
        api.translate(apiKey, text, "ru")
            .map { it.text.first() }
}

private interface YandexApi {

    @GET("/api/v1.5/tr.json/translate")
    fun translate(
        @Query("key") apiKey: String,
        @Query("text") text: String,
        @Query("lang") translationDirection: String
    ): Maybe<TranslateResponse>
}

private data class TranslateResponse(val code: Int, val lang: String, val text: List<String>)