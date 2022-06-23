package com.meltingb.medicare.api

import com.meltingb.medicare.data.CategoryResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoApiService {

    @GET("/v2/local/search/category.json")
    fun searchByCategory(
        @Header("Authorization") token: String,
        @Query("category_group_code") groupCode: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int
    ) : Single<CategoryResult>
}