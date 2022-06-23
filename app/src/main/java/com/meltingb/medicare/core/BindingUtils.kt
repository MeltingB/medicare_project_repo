package com.meltingb.medicare.core

import android.view.View
import androidx.databinding.BindingAdapter
import java.util.concurrent.atomic.AtomicBoolean

/**
 * binding double click prevent
 */
@BindingAdapter(value = ["OnSingleClick", "clickDelay"], requireAll = false)
fun View.setOnSingleClickListener(clickListener: View.OnClickListener?, clickDelay: Long) {
    //debug("setOnSingleClickListener $clickDelay")
    var _clickDelay = clickDelay
    if (_clickDelay == 0L) { //값을 입력하지 않으면 0 이므로 기본값으로 변경.
        _clickDelay = 1000
    }
    clickListener?.also {
        setOnClickListener(OnSingleClickListener(it, _clickDelay))
    } ?: setOnClickListener(null)
}

class OnSingleClickListener(
    private val clickListener: View.OnClickListener,
    private val intervalMs: Long = 1000
) : View.OnClickListener {
    private var canClick = AtomicBoolean(true)

    override fun onClick(v: View?) {
        if (canClick.getAndSet(false)) {
            v?.run {
                postDelayed({
                    canClick.set(true)
                }, intervalMs)
                clickListener.onClick(v)
            }
        }
    }
}