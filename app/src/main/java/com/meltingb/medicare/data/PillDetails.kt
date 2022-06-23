package com.meltingb.medicare.data

import android.os.Parcelable
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import kotlinx.android.parcel.Parcelize

@Xml(name = "response")
data class PillDetails(
    @Element
    val header: Header,
    @Element
    val body: Body,
)

@Xml(name = "header")
data class Header(
    @PropertyElement
    val resultCode: Int,
    @PropertyElement
    val resultMsg: String,
)

@Xml(name = "body")
data class Body(
    @Element
    val items: Items,
    @PropertyElement
    val numOfRows: Int,
    @PropertyElement
    val pageNo: Int,
    @PropertyElement
    val totalCount: Int,
)

@Xml
data class Items(
    @Element(name = "item")
    val item: List<Details>
)

@Xml
@Parcelize
data class Details(
    @PropertyElement(name = "itemSeq") var itemSeq: String?,
    @PropertyElement(name = "itemName") var itemName: String?,
    @PropertyElement(name = "efcyQesitm") var efcyQesitm: String?,
    @PropertyElement(name = "useMethodQesitm") var useMethodQesitm: String?,
    @PropertyElement(name = "atpnWarnQesitm") var atpnWarnQesitm: String?,
    @PropertyElement(name = "atpnQesitm") var atpnQesitm: String?,
    @PropertyElement(name = "intrcQesitm") var intrcQesitm: String?,
    @PropertyElement(name = "seQesitm") var seQesitm: String?,
    @PropertyElement(name = "depositMethodQesitm") var depositMethodQesitm: String?,
    @PropertyElement(name = "itemImage") var itemImage: String?
) : Parcelable {
    constructor() : this(
        null, null, null, null, null, null, null, null,
        null, null
    )
}