package com.meltingb.medicare.view

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.meltingb.medicare.R
import com.smarteist.autoimageslider.SliderViewAdapter


class PillSliderAdapter(context: Context, var list: List<Int>) :
    SliderViewAdapter<PillSliderAdapter.PillSliderViewHolder>() {

    inner class PillSliderViewHolder(parent: ViewGroup) : ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout, parent, false)
    ) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup): PillSliderViewHolder {
        return PillSliderViewHolder(parent)
    }

    override fun onBindViewHolder(viewHolder: PillSliderViewHolder, position: Int) {
        list[position].let {
            viewHolder.imageView.setBackgroundResource(it)
        }
    }
}