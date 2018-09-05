package com.wanglu.lib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow

open class BasePopup(popParams: PopParams) {

    // 默认设置pop的宽高都为wrap_content
    private val mPopup: PopupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    // 默认背景半透明数值为0.4f
    private var dim = 0.4f
    // 背景是否半透明
    private var isBgDim = false
    private var window: Window = popParams.activity.window
    private var windowAttr: WindowManager.LayoutParams
    private var bgDimAnimator: ValueAnimator
    private var clearBgDimAnimator: ValueAnimator
    private var animDuration = 300L

    init {
        windowAttr = window.attributes
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        mPopup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPopup.isFocusable = true

        mPopup.contentView = popParams.activity.layoutInflater.inflate(popParams.layoutRes, null)
        dim = popParams.dimValue
        isBgDim = popParams.isDim
        mPopup.setOnDismissListener {
            dismiss()
        }

        // 设置背景变暗的动画
        bgDimAnimator = ValueAnimator.ofFloat(1f, dim)
        bgDimAnimator.duration = animDuration
        bgDimAnimator.addUpdateListener { animation ->
            windowAttr.alpha = animation!!.animatedValue as Float
            window.attributes = windowAttr
        }


        // 设置背景恢复的动画
        clearBgDimAnimator = ValueAnimator.ofFloat(dim, 1f)
        clearBgDimAnimator.duration = animDuration
        clearBgDimAnimator.addUpdateListener { animation ->
            windowAttr.alpha = animation!!.animatedValue as Float
            window.attributes = windowAttr
        }
    }

    /**
     * 设置View
     */
    fun setContentView(view: View): BasePopup {
        mPopup.contentView = view
        return this
    }


    /**
     * 设置背景半透明数值
     */
    fun setDimValue(dimValue: Float): BasePopup {
        dim = dimValue
        return this
    }


    /**
     * 设置弹出时背景是否半透明
     */
    fun setIsBgDim(boolean: Boolean): BasePopup {
        isBgDim = boolean
        return this
    }


    /**
     * 消失掉并且还原背景透明
     */
    fun dismiss() {
        if (mPopup.isShowing)
            mPopup.dismiss()
        resetDim()
    }

    fun showAsDropDown(view: View) {
        setBgDim()
        mPopup.showAsDropDown(view)
    }

    /**
     * 获取contentView
     */
    fun getContentView(): View {
        return mPopup.contentView
    }

    fun getWindow(): Window {
        return window
    }

    fun getContext(): Context {
        return window.context
    }

    open fun show(view: View) {
        setBgDim()

//        mPopup.showAtLocation(view, Gravity.NO_GRAVITY,)
    }

    /**
     * 获取popupWindow显示的位置
     *
     * 首先 如果上下左右都满足show的条件，那么则在正下方
     * 其次 如果左右都有位置，那么显示在右面
     * 最后 上下都有位置，那么显示在下面
     */
    private fun getPopupShowLocation(view: View) {

        val result = IntArray(2)

        val viewLocation = IntArray(2)
        view.getLocationInWindow(viewLocation)
        val viewWidth = view.measuredWidth
        val viewHeight = view.measuredHeight

        // 屏幕大小
        val screenSize = Utils.getScreenSize(getContext())

        // popupWindow大小
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupContentViewHeight = getContentView().measuredHeight
        val popupContentViewWidth = getContentView().measuredWidth

        // 获取到点击的View的中心点
        result[0] = viewLocation[0] + viewWidth / 2
        result[1] = viewLocation[1] + viewHeight / 2

        // 判断点击view的左面和右面是否能够塞下popup
        if (screenSize[0] - result[0] > popupContentViewWidth / 2 &&
                result[0] > popupContentViewWidth / 2) {

        }
    }

    /**
     * 设置背景半透明
     */
    private fun setBgDim() {
        if (isBgDim) {
            bgDimAnimator.start()
        }
    }

    /**
     * 重置背景透明
     */
    private fun resetDim() {
        if (isBgDim) {
            clearBgDimAnimator.start()
        }
    }


}