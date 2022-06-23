package com.meltingb.medicare.api

import com.meltingb.medicare.data.PillDetails
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers("Accept: application/json")
    @GET("1470000/MdcinGrnIdntfcInfoService/getMdcinGrnIdntfcInfoList")
    fun searchByName(
        @Query("ServiceKey") serviceKey: String,
        @Query("item_name") name: String
    ): Flowable<PillInfo>

    @Headers("Accept: application/json")
    @GET("1470000/MdcinGrnIdntfcInfoService/getMdcinGrnIdntfcInfoList")
    fun searchBySeqNum(
        @Query("ServiceKey") serviceKey: String,
        @Query("item_seq") itemSeq: String
    ): Flowable<PillInfo>

    @Headers("Accept: application/json")
    @GET("1470000/MdcinGrnIdntfcInfoService/getMdcinGrnIdntfcInfoList")
    fun searchByCode(
        @Query("ServiceKey") serviceKey: String,
        @Query("edi_code") ediCode: String
    ): Flowable<PillInfo>

    @Headers("Accept: application/json")
    @GET("1471000/DrbEasyDrugInfoService/getDrbEasyDrugList")
    fun getDetailsBySeqNum(
        @Query("ServiceKey") serviceKey: String,
        @Query("itemSeq") itemSeq: String
    ) : Flowable<PillDetails>
}