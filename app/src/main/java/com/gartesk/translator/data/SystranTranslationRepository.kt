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
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class SystranTranslationRepository(context: Context) : TranslationRepository {

	private companion object {
		private const val API_URL = "https://api-platform.systran.net"
	}

	private val packageName = context.packageName

	private val api = Retrofit.Builder()
		.baseUrl(API_URL)
		.client(
			OkHttpClient.Builder()
				.addNetworkInterceptor(HttpLoggingInterceptor().apply { level = BODY })
				.build()
		)
		.addConverterFactory(GsonConverterFactory.create())
		.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
		.build()
		.create(SystranApi::class.java)

	override fun translate(text: Text, targetLanguage: Language): Single<Text> =
		api.translate(
			packageName,
			text.content,
			text.language.code,
			targetLanguage.code ?: throw IllegalArgumentException("Target language is null")
		)
			.map {
				val output = it.outputs.first()
				Text(content = output.output, language = targetLanguage)
			}

	override fun getDirections(): Single<List<Direction>> =
		api.listLanguages(packageName)
			.flatMapObservable { Observable.fromIterable(it.languagePairs) }
			.map { Direction(Language(it.source), Language(it.target)) }
			.toList()
}

private interface SystranApi {

	private companion object {
		private const val API_KEY_QUERY_KEY = "key"
		private const val API_KEY_QUERY_VALUE = BuildConfig.SYSTRAN_API_KEY
		private const val USER_AGENT_HEADER_KEY = "User-Agent"
		private const val USER_AGENT_HEADER_VALUE = "Android"
		private const val CERT_FINGERPRINT_QUERY_KEY = "certFingerprint"
		private const val CERT_FINGERPRINT_QUERY_VALUE = BuildConfig.CERT_FINGERPRINT
	}

	@GET("/translation/text/translate")
	fun translate(
		@Query("packageName") packageName: String,
		@Query("input") input: String,
		@Query("source") source: String?,
		@Query("target") target: String,
		@Query(CERT_FINGERPRINT_QUERY_KEY) certFingerprint: String = CERT_FINGERPRINT_QUERY_VALUE,
		@Query(API_KEY_QUERY_KEY) apiKey: String = API_KEY_QUERY_VALUE,
		@Header(USER_AGENT_HEADER_KEY) userAgent: String = USER_AGENT_HEADER_VALUE
	): Single<TranslateResponse>

	@GET("/translation/supportedLanguages")
	fun listLanguages(
		@Query("packageName") packageName: String,
		@Query(CERT_FINGERPRINT_QUERY_KEY) certFingerprint: String = CERT_FINGERPRINT_QUERY_VALUE,
		@Query(API_KEY_QUERY_KEY) apiKey: String = API_KEY_QUERY_VALUE,
		@Header(USER_AGENT_HEADER_KEY) userAgent: String = USER_AGENT_HEADER_VALUE
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