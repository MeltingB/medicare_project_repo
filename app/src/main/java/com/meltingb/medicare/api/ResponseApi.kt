package com.meltingb.medicare.api

import android.os.Parcelable
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import kotlinx.android.parcel.Parcelize

@Xml(name = "response")
data class PillInfo(
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
    val item: List<Item>
)

@Xml
@Parcelize
data class Item(
    @PropertyElement(name = "ITEM_SEQ") var itemSeq: String?,
    @PropertyElement(name = "ITEM_NAME") var itemName: String?,
    @PropertyElement(name = "ENTP_SEQ") var entpSeq: String?,
    @PropertyElement(name = "ENTP_NAME") var entpName: String?,
    @PropertyElement(name = "CHART") var chart: String?,
    @PropertyElement(name = "ITEM_IMAGE") var itemImage: String?,
    @PropertyElement(name = "PRINT_FRONT") var printFront: String?,
    @PropertyElement(name = "PRINT_BACK") var printBack: String?,
    @PropertyElement(name = "DRUG_SHAPE") var drugShape: String?,
    @PropertyElement(name = "COLOR_CLASS1") var colorClass1: String?,
    @PropertyElement(name = "COLOR_CLASS2") var colorClass2: String?,
    @PropertyElement(name = "LINE_FRONT") var lineFront: String?,
    @PropertyElement(name = "LINE_BACK") var lineBack: String?,
    @PropertyElement(name = "LENG_LONG") var lengLong: String?,
    @PropertyElement(name = "LENG_SHORT") var lengShort: String?,
    @PropertyElement(name = "THICK") var thick: String?,
    @PropertyElement(name = "IMG_REGIST_TS") var imgRegistTS: String?,
    @PropertyElement(name = "CLASS_NO") var classNo: String?,
    @PropertyElement(name = "ETC_OTC_NAME") var etcOtcName: String?,
    @PropertyElement(name = "FORM_CODE_NAME") var frontCodeName: String?,
    @PropertyElement(name = "MARK_CODE_FRONT_ANAL") var markCodeFrontAnal: String?,
    @PropertyElement(name = "MARK_CODE_BACK_ANAL") var markCodeBackAnal: String?,
    @PropertyElement(name = "MARK_CODE_FRONT_IMG") var markCodeFrontImg: String?,
    @PropertyElement(name = "MARK_CODE_BACK_IMG") var markCodeBackImg: String?,
    @PropertyElement(name = "ITEM_ENG_NAME") var itemEngName: String?,
    @PropertyElement(name = "MARK_CODE_FRONT") var markCodeFront: String?,
    @PropertyElement(name = "MARK_CODE_BACK") var markCodeBack: String?,
    @PropertyElement(name = "EDI_CODE") var ediCode: String?
): Parcelable {
    constructor() : this(
        null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null,
        null, null, null, null, null, null,
        null, null, null, null, null
    )
}