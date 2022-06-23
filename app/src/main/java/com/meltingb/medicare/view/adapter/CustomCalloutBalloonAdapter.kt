package com.meltingb.medicare.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.meltingb.medicare.R
import com.meltingb.medicare.data.Document
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem


internal class CustomCalloutBalloonAdapter(private var layoutInflater: LayoutInflater, var list: List<Document>) : CalloutBalloonAdapter {
    private val mCalloutBalloon: View = layoutInflater.inflate(R.layout.custom_callout_balloon, null)

    override fun getCalloutBalloon(poiItem: MapPOIItem): View {
        (mCalloutBalloon.findViewById(R.id.tv_place_name) as TextView).text = poiItem.itemName
        list.forEach {
            if(it.id.toInt() == poiItem.tag) {
                (mCalloutBalloon.findViewById(R.id.tv_place_name) as TextView).text = it.place_name
                (mCalloutBalloon.findViewById(R.id.tv_place_address) as TextView).text = it.address_name
                (mCalloutBalloon.findViewById(R.id.tv_place_tel) as TextView).text = it.phone
                return mCalloutBalloon
            }
        }
        return mCalloutBalloon
    }

    override fun getPressedCalloutBalloon(poiItem: MapPOIItem): View? {
        return null
    }

}