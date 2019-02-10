package com.gartesk.translator.data

import android.content.Context
import com.gartesk.translator.BuildConfig
import com.gartesk.translator.domain.entity.Direction
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

class SystranTranslationRepository(context: Context) : TranslationRepository {

    private val apiUrl = "https://api-platform.systran.net"
    private val apiKey = BuildConfig.SYSTRAN_API_KEY
    private val packageName = context.packageName
    private val certFingerprint = BuildConfig.CERT_FINGERPRINT

    private val api = Retrofit.Builder()
        .baseUrl(apiUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val modifiedRequest = originalRequest.newBuilder()
                        .addHeader("User-Agent", "Android")
                        .build()
                    chain.proceed(modifiedRequest)
                }
                .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(SystranApi::class.java)

    override fun translate(text: Text, targetLanguage: Language): Single<Text> =
        api.translate(
            packageName,
            certFingerprint,
            apiKey,
            text.content,
            text.language.code,
            targetLanguage.code ?: throw IllegalArgumentException("Target language is null")
        )
            .map {
                val output = it.outputs.first()
                Text(content = output.output, language = targetLanguage)
            }

    override fun getDirections(): Single<List<Direction>> =
        api.listLanguages(packageName, certFingerprint, apiKey)
            .flatMapObservable { Observable.fromIterable(it.languagePairs) }
            .map { Direction(Language(it.source), Language(it.target)) }
            .toList()
}

private interface SystranApi {

    @GET("/translation/text/translate")
    fun translate(
        @Query("packageName") packageName: String,
        @Query("certFingerprint") certFingerprint: String,
        @Query("key") apiKey: String,
        @Query("input") input: String,
        @Query("source") source: String?,
        @Query("target") target: String
    ): Single<TranslateResponse>

    @GET("/translation/supportedLanguages")
    fun listLanguages(
        @Query("packageName") packageName: String,
        @Query("certFingerprint") certFingerprint: String,
        @Query("key") apiKey: String
    ): Single<SupportedLanguagesResponse>
}

private data class ErrorModel(val message: String, val info: ErrorInfoModel)
private data class ErrorInfoModel(val statusCode: Int, val message: String)

private data class TranslateResponse(
    val error: ErrorModel,
    val requestId: String,
    val outputs: List<TranslationModel>
)

private data class TranslationModel(
    val error: String?,
    val detectedLanguage: String?,
    val output: String
)

private data class SupportedLanguagesResponse(
    val error: ErrorModel,
    val languagePairs: List<LanguagePairModel>
)

private data class LanguagePairModel(val source: String, val target: String)