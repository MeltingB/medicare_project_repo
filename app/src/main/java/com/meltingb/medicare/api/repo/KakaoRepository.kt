package com.meltingb.medicare.api.repo

import com.meltingb.medicare.api.KakaoApiService
import com.meltingb.medicare.data.CategoryResult
import com.meltingb.medicare.utils.KAKAO_REST_API_KEY
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoRepository {

    fun searchByCategory(groupCode: String, x: String, y: String, radius: Int): Single<CategoryResult>

}

class KakaoRepositoryImpl(private val kakaoApi: KakaoApiService) : KakaoRepository {
    override fun searchByCategory(groupCode: String, x: String, y: String, radius: Int): Single<CategoryResult> {
        return kakaoApi.searchByCategory(
            "KakaoAK $KAKAO_REST_API_KEY", groupCode, x, y, radius
        )
    }

}