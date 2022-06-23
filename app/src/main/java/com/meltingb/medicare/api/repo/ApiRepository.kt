package com.meltingb.medicare.api.repo

import com.meltingb.medicare.api.ApiService
import com.meltingb.medicare.api.PillInfo
import com.meltingb.medicare.data.PillDetails
import com.meltingb.medicare.utils.API_DEV_KEY
import io.reactivex.Flowable

interface ApiRepository {

    fun searchByName(name: String): Flowable<PillInfo>

    fun searchBySeqNum(seqNum: String): Flowable<PillInfo>

    fun searchByEdiCode(code: String): Flowable<PillInfo>

    fun getDetailsBySeqNum(seqNum: String): Flowable<PillDetails>
}

class ApiRepositoryImpl(var api: ApiService) : ApiRepository {
    override fun searchByName(name: String): Flowable<PillInfo> {
        return api.searchByName(API_DEV_KEY, name)
    }

    override fun searchBySeqNum(seqNum: String): Flowable<PillInfo> {
        return api.searchBySeqNum(API_DEV_KEY, seqNum)
    }

    override fun searchByEdiCode(code: String): Flowable<PillInfo> {
        return api.searchByCode(API_DEV_KEY, code)
    }

    override fun getDetailsBySeqNum(seqNum: String): Flowable<PillDetails> {
        return api.getDetailsBySeqNum(API_DEV_KEY, seqNum)
    }

}
