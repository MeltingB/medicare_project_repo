package com.meltingb.medicare.di

import com.google.gson.GsonBuilder
import com.meltingb.medicare.api.ApiService
import com.meltingb.medicare.api.DateTimeConverter
import com.meltingb.medicare.api.KakaoApiService
import com.meltingb.medicare.api.repo.ApiRepository
import com.meltingb.medicare.api.repo.ApiRepositoryImpl
import com.meltingb.medicare.api.repo.KakaoRepository
import com.meltingb.medicare.api.repo.KakaoRepositoryImpl
import com.meltingb.medicare.db.RoomDataBase
import com.meltingb.medicare.db.repo.PillRepository
import com.meltingb.medicare.db.repo.PillRepositoryImpl
import com.meltingb.medicare.view.viewmodel.*
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val DB_NAME = "MEDICARE_DB"
const val BASE_URL = "http://apis.data.go.kr/"
const val KAKAO_URL = "https://dapi.kakao.com/"

val kakaoModule = module(override = true) {
    val gson = GsonBuilder().registerTypeHierarchyAdapter(
        DateTime::class.java,
        DateTimeConverter()
    ).setDateFormat("yyyy-MM-dd HH:mm:ssZ").create()

    fun getKakaoRetrofitBuild(client: OkHttpClient) = Retrofit.Builder().run {
        baseUrl(KAKAO_URL)
        client(client)
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        addConverterFactory(GsonConverterFactory.create(gson))
        build()
    }

    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
        }

        return builder.build()
    }

    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    single { provideLoggingInterceptor() }
    single { provideOkHttpClient(get()) }
    single { getKakaoRetrofitBuild(get()) }

    fun kakaoApiService(retrofit: Retrofit): KakaoApiService = retrofit.create(KakaoApiService::class.java)

    factory { kakaoApiService(getKakaoRetrofitBuild(get())) }
}

val apiModule = module(override = true) {

    val gson = GsonBuilder().registerTypeHierarchyAdapter(
        DateTime::class.java,
        DateTimeConverter()
    ).setDateFormat("yyyy-MM-dd HH:mm:ssZ").create()

    fun getRetrofitBuild(client: OkHttpClient) = Retrofit.Builder().run {
        baseUrl(BASE_URL)
        client(client)
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
        build()
    }

    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
        }

        return builder.build()
    }

    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    single { provideLoggingInterceptor() }
    single { provideOkHttpClient(get()) }
    single { getRetrofitBuild(get()) }

    fun apiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    factory { apiService(getRetrofitBuild(get())) }
}

val databaseModule = module {
    single { RoomDataBase.getDatabase(get(), DB_NAME) }
    single { get<RoomDataBase>().pillDao() }

}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { AddViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { MapViewModel(get()) }
    viewModel { TakeDetailViewModel(get()) }
    viewModel { EditViewModel(get()) }
}

val repositoryModule = module {
    factory { PillRepositoryImpl(get()) as PillRepository }
    factory { ApiRepositoryImpl(get()) as ApiRepository }
    factory { KakaoRepositoryImpl(get()) as KakaoRepository }
}

val appModules = listOf(viewModelModule, databaseModule, repositoryModule, apiModule, kakaoModule)
