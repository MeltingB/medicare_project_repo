package com.meltingb.medicare.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Scroller
import android.widget.TextView
import androidx.annotation.StringRes


/**
 * Title 이 없는 Custom Dialog
 * initView() : Message TextView, Left/Right Button init > 버튼 한개 또는 두개 지정 가능
 * setMessage() : Dialog message
 * setLeftButton() : 왼쪽 버튼 또는 버튼이 한개인 경우
 * setRightButton() : 오른쪽 버튼
 * setCancelable() : 화면 터치 시 Dialog dismiss 여부
 *
 */
class NoTitleDialogBuilder(context: Context, resource: Int) {

    private val mContext = context
    lateinit var tvTitle: TextView
    lateinit var tvContent: TextView
    lateinit var btnLeft: Button
    lateinit var btnRight: Button

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }

    private val view: View by lazy {
        View.inflate(context, resource, null)
    }
    var dialog: AlertDialog? = null

    fun initView(contentId: Int, btnId: Int) {
        tvContent = view.findViewById(contentId)
        btnLeft = view.findViewById(btnId)
    }

    fun initView(contentId: Int, leftBtnId: Int, rightBtnId: Int) {
        tvContent = view.findViewById(contentId)
        btnLeft = view.findViewById(leftBtnId)
        btnRight = view.findViewById(rightBtnId)
    }

    fun setMessage(@StringRes messageId: Int): NoTitleDialogBuilder {
        tvContent.text = mContext.getText(messageId)
        return this
    }

    fun setMessage(message: CharSequence): NoTitleDialogBuilder {
        tvContent.text = message
        return this
    }

    fun setLeftButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): NoTitleDialogBuilder {
        btnLeft.apply {
            text = mContext.getText(textId)
            setOnClickListener(listener)
        }
        return this
    }

    fun setLeftButton(text: CharSequence, listener: (view: View) -> (Unit)): NoTitleDialogBuilder {
        btnLeft.apply {
            this.text = text
            setOnClickListener(listener)
        }
        return this
    }

    fun setRightButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): NoTitleDialogBuilder {
        btnRight.apply {
            text = mContext.getText(textId)
            this.text = text
            setOnClickListener(listener)
        }
        return this
    }

    fun setRightButton(text: CharSequence, listener: (view: View) -> (Unit)): NoTitleDialogBuilder {
        btnRight.apply {
            this.text = text
            setOnClickListener(listener)
        }
        return this
    }

    fun setCancelable(boolean: Boolean) {
        dialog?.setCancelable(boolean)
    }

    fun setScrollable() {
        tvContent.setScroller(Scroller(mContext))
        tvContent.isVerticalScrollBarEnabled = true
        tvContent.movementMethod = ScrollingMovementMethod()
    }

    fun create() {
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show(ratio: Double) {
        dialog?.show()

        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        size.x // 디바이스 가로 길이

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * ratio).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}