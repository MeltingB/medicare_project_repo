package com.meltingb.medicare.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.meltingb.medicare.R
import com.meltingb.medicare.api.Item
import com.meltingb.medicare.data.PillEntity
import java.time.LocalDate

class SearchListViewAdapter(var context: Context, var list: List<Item>) : RecyclerView.Adapter<SearchListViewAdapter.ViewHolder>() {
    private lateinit var clickListener: RecyclerViewItemClickListener
    interface RecyclerViewItemClickListener{
        fun onClick(view: View, position: Int)
    }

    fun setItemClickListener(listener: RecyclerViewItemClickListener) {
        this.clickListener = listener
    }


    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
    ) {
        val pillImageView: ImageView = itemView.findViewById(R.id.iv_pill)
        val pillName: TextView = itemView.findViewById(R.id.tv_pill_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].let {
            with(holder) {
                if(it.itemImage != null) {
                    Glide.with(context)
                        .load(it.itemImage)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(pillImageView)
                }
                pillName.text = it.itemName


                itemView.setOnClickListener {
                    clickListener.onClick(it, position)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}