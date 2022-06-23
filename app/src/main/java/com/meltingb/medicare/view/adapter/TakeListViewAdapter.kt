package com.meltingb.medicare.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meltingb.medicare.R
import com.meltingb.medicare.data.PillEntity

class TakeListViewAdapter(var list: List<PillEntity>): RecyclerView.Adapter<TakeListViewAdapter.TakeListViewHolder>() {

    inner class TakeListViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_take, parent, false)
    ) {
        val pillName: TextView = itemView.findViewById(R.id.tv_name)
        val takeInfo: TextView = itemView.findViewById(R.id.tv_takeInfo)
        val takeTime: TextView = itemView.findViewById(R.id.tv_takeTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TakeListViewHolder {
        return TakeListViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TakeListViewHolder, position: Int) {
        list[position].let {
            with(holder) {
                pillName.text = it.pillName
                takeInfo.text = "${it.takeNum}${it.takeType}"
                takeTime.text = it.alarmTime
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}