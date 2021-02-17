package com.gartesk.translator.data.translation

import com.gartesk.translator.BuildConfig
import com.gartesk.translator.domain.entity.Language
import com.gartesk.translator.domain.entity.Text
import com.gartesk.translator.domain.entity.Translation
import com.gartesk.translator.domain.repository.TranslationRepository
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

class GoogleTranslationRepository : TranslationRepository {

	private companion object {
		private const val API_URL = "https://translation.googleapis.com/language/translate/v2/"
		private const val CONNECT_TIMEOUT_MILLIS = 10000L
		private const val WRITE_TIMEOUT_MILLIS = 30000L
		private const val READ_TIMEOUT_MILLIS = 30000L
	}

	private val api = Retrofit.Builder()
		.baseUrl(API_URL)
		.client(
			OkHttpClient.Builder()
				.addNetworkInterceptor(HttpLoggingInterceptor().apply { level = BODY })
				.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
				.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
				.writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
				.build()
		)
		.addConverterFactory(GsonConverterFactory.create())
		.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
		.build()
		.create(GoogleApi::class.java)

	override fun translate(textFrom: String, languageTo: Language): Single<Translation> =
		api.translate(TranslateRequest(listOf(textFrom), languageTo.code))
			.map { response ->
				val firstTranslation = response.translations.first()
				Translation(
					from = Text(textFrom, Language(firstTranslation.detectedSourceLanguage)),
					to = Text(firstTranslation.translatedText, languageTo)
				)
			}

	override fun getLanguages(): Single<List<Language>> =
		api.listLanguages()
			.flatMapObservable { Observable.fromIterable(it.languages) }
			.map { Language(it.language) }
			.toList()
}

private interface GoogleApi {

	private companion object {
		private const val AUTHORIZATION_HEADER_KEY = "Authorization"
		private const val AUTHORIZATION_HEADER_VALUE = BuildConfig.GCLOUD_TOKEN
	}

	@GET("translation/text/translate")
	fun translate(
		@Body translateRequest: TranslateRequest,
		@Header(AUTHORIZATION_HEADER_KEY) authorization: String = AUTHORIZATION_HEADER_VALUE
	): Single<TranslateResponse>

	@GET("languages")
	fun listLanguages(
		@Header(AUTHORIZATION_HEADER_KEY) authorization: String = AUTHORIZATION_HEADER_VALUE
	): Single<SupportedLanguagesResponse>
}

private data class ErrorModel(val message: String, val info: ErrorInfoModel)
private data class ErrorInfoModel(val statusCode: Int, val message: String)

private data class TranslateRequest(
	@SerializedName("q") val query: List<String>,
	val target: String
)

private data class TranslateResponse(
	val error: ErrorModel,
	val translations: List<TranslationModel>
)

private data class TranslationModel(
	val detectedSourceLanguage: String,
	val translatedText: String
)

private data class SupportedLanguagesResponse(
	val error: ErrorModel,
	val languages: List<LanguageModel>
)

private data class LanguageModel(val language: String)