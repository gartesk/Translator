package com.gartesk.translator.data

import com.gartesk.translator.BuildConfig
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.repository.TranslationRepository
import io.reactivex.Observable
import io.reactivex.Single
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

    override fun translate(text: Text, targetLanguage: Language): Single<Text> {
        val langParam =
            if (text.language.isUnknown) targetLanguage.code
            else "${text.language.code}-${targetLanguage.code}"

        return api.translate(apiKey, text.content, langParam)
            .map { Text(content = it.text.first(), language = Language(it.lang)) }
    }

    override fun listLanguages(outputLanguage: Language): Single<List<Language>> =
            api.listLanguages(apiKey, outputLanguage.code)
                .flatMapObservable { Observable.fromIterable(it.langs.keys) }
                .map { Language(it) }
                .toList()
}

private interface YandexApi {

    @GET("/api/v1.5/tr.json/translate")
    fun translate(
        @Query("key") apiKey: String,
        @Query("text") text: String,
        @Query("lang") translationDirection: String?
    ): Single<TranslateResponse>

    @GET("/api/v1.5/tr.json/getLangs")
    fun listLanguages(
        @Query("key") apiKey: String,
        @Query("ui") uiLanguage: String?
    ): Single<ListLanguagesResponse>
}

private data class TranslateResponse(val code: Int, val lang: String, val text: List<String>)
private data class ListLanguagesResponse(val code: Int, val langs: Map<String, String>)