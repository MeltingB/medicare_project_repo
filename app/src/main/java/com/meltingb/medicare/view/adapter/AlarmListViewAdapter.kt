package com.meltingb.medicare.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meltingb.medicare.R
import com.meltingb.medicare.data.PillEntity

class AlarmListViewAdapter(var list: MutableList<PillEntity>, private var selectedDate: String) : RecyclerView.Adapter<AlarmListViewAdapter.ViewHolder>() {
    private lateinit var clickListener: RecyclerViewItemClickListener
    private lateinit var changeListener: CheckboxChangeListener

    interface RecyclerViewItemClickListener {
        fun onClick(view: View, position: Int)
    }

    interface CheckboxChangeListener {
        fun onChanged(position: Int, isChecked: Boolean)
    }

    fun setItemClickListener(listener: RecyclerViewItemClickListener) {
        this.clickListener = listener
    }

    fun setCheckboxChangeListener(listener: CheckboxChangeListener) {
        this.changeListener = listener
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
    ) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iv_icon)
        val pillName: TextView = itemView.findViewById(R.id.tv_pill_name)
        val pillInfo: TextView = itemView.findViewById(R.id.tv_pill_info)
        val checkBox: CheckBox = itemView.findViewById(R.id.btn_take)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].let {
            with(holder) {
                when (it.pillImageNum) {
                    0 -> iconImageView.setBackgroundResource(R.drawable.ic_pill1)
                    1 -> iconImageView.setBackgroundResource(R.drawable.ic_pill2)
                    2 -> iconImageView.setBackgroundResource(R.drawable.ic_vitamin)
                }
                pillName.text = it.pillName
                pillInfo.text = "${it.takeNum}${it.takeType} / ${it.alarmTime}"
                checkBox.isChecked = it.takeDayList.contains(selectedDate)

                itemView.setOnClickListener { view ->
                    clickListener.onClick(view, position)
                }

                checkBox.setOnClickListener { _ ->
                    changeListener.onChanged(position, checkBox.isChecked)
                    val takeList = it.takeDayList.toMutableList()
                    if (it.takeDayList.contains(selectedDate)) {
                        takeList.remove(selectedDate)
                        list[position].takeDayList = takeList
                    } else {
                        takeList.add(selectedDate)
                        list[position].takeDayList = takeList
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}