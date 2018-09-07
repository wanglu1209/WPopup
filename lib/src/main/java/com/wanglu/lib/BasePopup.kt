package com.wanglu.lib

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.AbsListView
import android.widget.PopupWindow


open class BasePopup(val popParams: WPopParams) : View.OnTouchListener {

    // 默认设置pop的宽高都为wrap_content
    private val mPopup: PopupWindow
    // 默认背景半透明数值为0.4f
    private var dim = 0.4f
    // 背景是否半透明
    private var isBgDim = false
    private var window: Window = popParams.activity.window
    private var windowAttr: WindowManager.LayoutParams
    private var bgDimAnimator: ValueAnimator
    private var clearBgDimAnimator: ValueAnimator
    // 背景透明动画时间
    private var animDuration = 200L

    private val clickLocation = FloatArray(2)

    private lateinit var popupContentViewSize: IntArray

    var defaultMargin: Int

    init {
        windowAttr = window.attributes
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        mPopup = PopupWindow(popParams.width, popParams.height)
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



        if (popParams.longClickView != null) {
            // 判断是否是ListView或者GridView
            if (popParams.longClickView!! is AbsListView) {
                // 拦截点击事件获取坐标
                (popParams.longClickView!! as AbsListView).setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            clickLocation[0] = event.rawX
                            clickLocation[1] = event.rawY
                        }
                    }
                    false
                }
            } else if (popParams.longClickView!! is RecyclerView) {
                // 判断是不是RecyclerView 拦截点击事件获取坐标
                (popParams.longClickView!! as RecyclerView).addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) {

                    }

                    override fun onInterceptTouchEvent(p0: RecyclerView, event: MotionEvent): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                clickLocation[0] = event.rawX
                                clickLocation[1] = event.rawY
                            }
                        }
                        return false
                    }

                    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {

                    }

                })
            } else
                popParams.longClickView!!.setOnTouchListener(this)
        }

        mPopup.animationStyle = popParams.animRes

        /**
         * 设置点击外部取消
         */
        if (!popParams.cancelable) {
            //from https://github.com/pinguo-zhouwei/CustomPopwindow
            mPopup.isFocusable = true
            mPopup.isOutsideTouchable = false
            mPopup.setBackgroundDrawable(null)
            //注意下面这三个是contentView 不是PopupWindow，响应返回按钮事件
            getContentView().isFocusable = true
            getContentView().isFocusableInTouchMode = true
            getContentView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()

                    return@OnKeyListener true
                }
                false
            })
            //在Android 6.0以上 ，只能通过拦截事件来解决
            mPopup.setTouchInterceptor(OnTouchListener { v, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (event.action == MotionEvent.ACTION_DOWN && (x < 0 || x >= popupContentViewSize[0] || y < 0 || y >= popupContentViewSize[1])) {
                    //outside
                    Log.d(TAG, "onTouch outside:mWidth=${popupContentViewSize[0]},mHeight=${popupContentViewSize[1]}")
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    //outside
                    Log.d(TAG, "onTouch outside:mWidth=${popupContentViewSize[0]},mHeight=${popupContentViewSize[1]}")
                    return@OnTouchListener true
                }
                false
            })
        } else {
            mPopup.isFocusable = true
            mPopup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        defaultMargin = Utils.dp2px(getContext(), 10)
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

    /**
     * 根据view的位置来自动选择弹出位置
     */
    open fun showAtView(view: View) {
        setBgDim()

        val location = getPopupShowLocation(view)
        mPopup.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1])
    }


    /**
     * 获取popupWindow显示的位置
     *
     * 首先 如果上下左右都满足show的条件，那么则在正下方
     * 其次 如果左右都有位置，那么显示在中间
     * 最后 上下都有位置，那么显示在下面
     */
    fun getPopupShowLocation(view: View): IntArray {

        val result = IntArray(2)

        val viewLocation = getViewLocation(view)
        val viewWidth = view.measuredWidth
        val viewHeight = view.measuredHeight

        // window大小
        val windowSize = Utils.getWindowSize(popParams.activity)

        popupContentViewSize = getPopupContentViewSize()

        // 获取到点击的View的中心点
        result[0] = viewLocation[0] + viewWidth / 2
        result[1] = viewLocation[1] + viewHeight / 2

        // 判断点击view的左面和右面是否能够塞下popup
        when {
            windowSize[0] - result[0] > popupContentViewSize[0] / 2 && result[0] > popupContentViewSize[0] / 2 -> {
                // 如果左面和右面都能塞下，那么X轴直接就放到中间， 计算出X坐标
                result[0] -= popupContentViewSize[0] / 2
            }
            windowSize[0] - result[0] > popupContentViewSize[0] / 2 -> {
                // 如果右面能塞下，那么X坐标则和点击的View的X坐标一样
                result[0] = viewLocation[0]
            }
            result[0] > popupContentViewSize[0] / 2 -> {
                // 如果左面能塞下，那么X坐标则和点击的View的右侧X坐标一样
                result[0] = viewLocation[0] + viewWidth - popupContentViewSize[0]
            }
        }

        // 判断上下是否能塞下popup
        when {
            (windowSize[1] - result[1] > popupContentViewSize[1] / 2 && result[1] > popupContentViewSize[1] / 2) || windowSize[1] - result[1] > popupContentViewSize[1] / 2 -> {
                // 如果上下都能塞下或者只有下面能塞下，那么则默认放在下面
                result[1] += viewHeight / 2
            }
            result[1] > popupContentViewSize[1] / 2 -> {
                // 如果上面能塞下
                result[1] = viewLocation[1] - popupContentViewSize[1]
            }
        }
        return result

    }

    /**
     * 获取popup显示的方向  上还是下
     */
    fun getShowDirection(view: View): Int {
        val viewLocation = getViewLocation(view)
        val viewHeight = view.measuredHeight

        // window大小
        val windowSize = Utils.getWindowSize(popParams.activity)

        val popupContentViewHeight = getPopupContentViewSize()[0]


        // 判断显示的位置是上还是下
        when {
            windowSize[1] - (viewLocation[1] + viewHeight / 2) > popupContentViewHeight / 2 ->
                return WPopupDirection.BOTTOM
            (viewLocation[1] + viewHeight / 2) > popupContentViewHeight / 2 ->
                return WPopupDirection.TOP
        }

        return WPopupDirection.BOTTOM
    }


    /**
     * 根据view位置显示
     */
    open fun showAtDirectionByView(view: View, direction: Int) {
        setBgDim()
        val result = IntArray(2)

        val viewLocation = IntArray(2)
        view.getLocationInWindow(viewLocation)
        val viewWidth = view.measuredWidth
        val viewHeight = view.measuredHeight


        popupContentViewSize = getPopupContentViewSize()


        when (direction) {
            WPopupDirection.LEFT -> {
                result[0] = viewLocation[0] - popupContentViewSize[0] - Utils.dp2px(getContext(), popParams.commonPopMargin)
                result[1] = viewLocation[1] + (viewHeight / 2) - (popupContentViewSize[1] / 2)
            }
            WPopupDirection.RIGHT -> {
                result[0] = viewLocation[0] + viewWidth + Utils.dp2px(getContext(), popParams.commonPopMargin)
                result[1] = viewLocation[1] + viewHeight / 2 - popupContentViewSize[1] / 2
            }
            WPopupDirection.BOTTOM -> {
                result[0] = viewLocation[0] + viewWidth / 2 - popupContentViewSize[0] / 2
                result[1] = viewLocation[1] + viewHeight + Utils.dp2px(getContext(), popParams.commonPopMargin)
            }
            WPopupDirection.TOP -> {
                result[0] = viewLocation[0] + (viewWidth / 2) - popupContentViewSize[0] / 2
                result[1] = viewLocation[1] - popupContentViewSize[1] - Utils.dp2px(getContext(), popParams.commonPopMargin)
            }
        }

        mPopup.showAtLocation(view, Gravity.NO_GRAVITY, result[0], result[1])
    }

    /**
     * 根据手指长按位置来show
     */
    open fun showAtFingerLocation() {
        popupContentViewSize = getPopupContentViewSize()
        val windowSize = Utils.getWindowSize(popParams.activity)
        val result = IntArray(2)

        // 判断点击view的左面和右面是否能够塞下popup
        when {
            (windowSize[0] - clickLocation[0] > popupContentViewSize[0] && windowSize[0] > popupContentViewSize[0]) || (windowSize[0] - clickLocation[0] > popupContentViewSize[0]) ->
                // 如果左面和右面都能塞下或者右面能塞下，那么X轴直接就是点击的位置
                result[0] = clickLocation[0].toInt()
            else -> {
                result[0] = clickLocation[0].toInt() - popupContentViewSize[0]
            }
        }

        // 判断上下是否能塞下popup
        when {
            (windowSize[1] - clickLocation[1] > popupContentViewSize[1] && windowSize[1] > popupContentViewSize[1]) || windowSize[1] - clickLocation[1] > popupContentViewSize[1] -> {
                // 如果上下都能塞下或者只有下面能塞下，那么则默认放在下面
                result[1] = clickLocation[1].toInt()
            }
            else -> {
                // 如果上面能塞下
                result[1] = (clickLocation[1] - popupContentViewSize[1]).toInt()
            }
        }

        mPopup.showAtLocation(getContentView(), Gravity.NO_GRAVITY, result[0], result[1])
    }

    /**
     * 根据手指长按位置和设定的方向来show
     */
    open fun showAtFingerLocation(direction: Int) {
        popupContentViewSize = getPopupContentViewSize()
        val result = IntArray(2)


        when (direction) {
            WPopupDirection.TOP -> {
                // 正上方
                result[0] = (clickLocation[0] - (popupContentViewSize[0] / 2)).toInt()
                result[1] = (clickLocation[1] - popupContentViewSize[1]).toInt() - defaultMargin
            }
            WPopupDirection.LEFT -> {
                // 正左方
                result[0] = (clickLocation[0] - popupContentViewSize[0]).toInt() - defaultMargin
                result[1] = (clickLocation[1] - popupContentViewSize[1] / 2).toInt()
            }
            WPopupDirection.BOTTOM -> {
                // 正下方
                result[0] = (clickLocation[0] - (popupContentViewSize[0] / 2)).toInt()
                result[1] = clickLocation[1].toInt() + defaultMargin
            }
            WPopupDirection.RIGHT -> {
                // 正右方
                result[0] = clickLocation[0].toInt() + defaultMargin
                result[1] = (clickLocation[1] - popupContentViewSize[1] / 2).toInt()
            }
            WPopupDirection.LEFT_TOP -> {
                // 左上方
                result[0] = (clickLocation[0] - popupContentViewSize[0]).toInt() - defaultMargin
                result[1] = (clickLocation[1] - popupContentViewSize[1]).toInt() - defaultMargin
            }
            WPopupDirection.LEFT_BOTTOM -> {
                // 左下方
                result[0] = (clickLocation[0] - popupContentViewSize[0]).toInt() - defaultMargin
                result[1] = clickLocation[1].toInt() + defaultMargin
            }
            WPopupDirection.RIGHT_TOP -> {
                result[0] = clickLocation[0].toInt() + defaultMargin
                result[1] = (clickLocation[1] - popupContentViewSize[1]).toInt() - defaultMargin
            }
            WPopupDirection.RIGHT_BOTTOM -> {
                result[0] = clickLocation[0].toInt() + defaultMargin
                result[1] = clickLocation[1].toInt() + defaultMargin
            }
        }

        mPopup.showAtLocation(getContentView(), Gravity.NO_GRAVITY, result[0], result[1])
    }

    /**
     * 通过方向show出来 上下
     */
    fun showAtDirection(direction: Int) {
        setBgDim()
        val result = IntArray(2)
        popupContentViewSize = getPopupContentViewSize()
        val windowSize = Utils.getWindowSize(popParams.activity)
        when (direction) {
            WPopupDirection.BOTTOM -> {
                result[0] = 0
                result[1] = windowSize[1] - popupContentViewSize[1]
            }
            WPopupDirection.TOP -> {
                result[0] = 0
                result[1] = 0
            }
        }
        mPopup.animationStyle = WPopupAnim.ANIM_SCALE_Y
        mPopup.showAtLocation(getContentView().rootView, Gravity.NO_GRAVITY, result[0], result[1])
    }


    private fun getViewLocation(view: View): IntArray {
        val viewLocation = IntArray(2)
        view.getLocationInWindow(viewLocation)
        return viewLocation
    }

    /**
     * 获取contentView的大小
     */
    private fun getPopupContentViewSize(): IntArray {
        // popupWindow大小
        getContentView().measure(0, 0)
        var popupContentViewHeight = getContentView().measuredHeight
        var popupContentViewWidth = getContentView().measuredWidth + defaultMargin

        if (popParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupContentViewWidth = Utils.getWindowSize(popParams.activity)[0]
        }

        return intArrayOf(popupContentViewWidth, popupContentViewHeight)
    }

    /**
     * 设置动画
     */
    fun setAnim(anim: Int) {
        popParams.animRes = anim
        mPopup.animationStyle = popParams.animRes!!
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


    fun <T : View> findViewById(resId: Int): T {
        return getContentView().findViewById(resId)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                clickLocation[0] = event.rawX
                clickLocation[1] = event.rawY
                Log.d("112233", "${clickLocation[0]} --- ${clickLocation[1]}")
            }
        }
        return false
    }

}