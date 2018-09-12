package com.wanglu.lib

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RotateDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.wanglu.lib.rvDivider.HorizontalDividerItemDecoration
import com.wanglu.lib.rvDivider.VerticalDividerItemDecoration


class WPopup(popParams: WPopParams) : BasePopup(popParams) {

    private var commonAdapter: WPopupAdapter = WPopupAdapter(this)
    private var recyclerView: RecyclerView
    private val commonRootLayout: LinearLayout
    private var triangle: ImageView? = null
    private var oldDirection = -100

    init {
        commonAdapter.setData(popParams.mCommonData!!)
        if (popParams.mWItemClickListener != null){
            commonAdapter.setItemClickListener(popParams.mWItemClickListener!!)
        }else{
            Log.e("WPopup", "No item clickListener.")
        }
        commonAdapter.setDirection(popParams.commonIconDirection)
        commonAdapter.setTextColor(popParams.commonItemTextColor)
        commonAdapter.setTextSize(popParams.commonItemTextSize)
        commonAdapter.setDrawablePadding(popParams.commonDraablePadding)
        recyclerView = getContentView().findViewById(R.id.mRvCommon)
        commonRootLayout = getContentView().findViewById(R.id.mCommonRootLayout)
        recyclerView.adapter = commonAdapter
        recyclerView.layoutManager = LinearLayoutManager(getContext(), popParams.commonPopupOrientation, false)
        when (popParams.commonPopupOrientation) {
            LinearLayoutManager.HORIZONTAL ->
                recyclerView.addItemDecoration(VerticalDividerItemDecoration.Builder(getContext()).margin(popParams.commonPopupDividerMargin).size(popParams.commonPopupDividerSize).color(popParams.commonPopupDividerColor).build())
            LinearLayoutManager.VERTICAL ->
                recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(getContext()).margin(popParams.commonPopupDividerMargin).size(popParams.commonPopupDividerSize).color(popParams.commonPopupDividerColor).build())
        }

    }


    override fun showAtView(view: View) {
        val showDirection = getShowDirection(view)
        setTriangle(view, showDirection)
        super.showAtView(view)
    }

    override fun showAtFingerLocation(direction: Int) {
        if (direction == WPopupDirection.TOP || direction == WPopupDirection.RIGHT_TOP || direction == WPopupDirection.LEFT_TOP)
            setTriangle(popParams.longClickView!!, WPopupDirection.TOP)
        else if (direction == WPopupDirection.LEFT_BOTTOM || direction == WPopupDirection.BOTTOM || direction == WPopupDirection.RIGHT_BOTTOM)
            setTriangle(popParams.longClickView!!, WPopupDirection.BOTTOM)
        super.showAtFingerLocation(direction)
    }

    override fun showAtDirectionByView(view: View, direction: Int) {
        if (direction == WPopupDirection.TOP || direction == WPopupDirection.BOTTOM)
            setTriangle(view, direction)
        else {
            if (triangle != null) {
                commonRootLayout.removeView(triangle)
                triangle = null
            }
            oldDirection = direction
        }
        super.showAtDirectionByView(view, direction)
    }


    /**
     * 设置三角形
     */
    private fun setTriangle(view: View, showDirection: Int) {


        // 设置三角形的边距
        val params: LinearLayout.LayoutParams
        val viewLocation = IntArray(2)
        view.getLocationInWindow(viewLocation)
        val viewWidth = view.measuredWidth
        val showLocation = getPopupShowLocation(view)
        val margin = viewLocation[0] + viewWidth / 2 - showLocation[0] - defaultMargin


        // 判断显示位置来添加三角形 只有上下才添加
        if (showDirection != oldDirection) {
            if (triangle != null) {
                commonRootLayout.removeView(triangle)
                triangle = null
            }

            if (triangle == null) {
                params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.leftMargin = margin
                triangle = ImageView(view.context)
                if (showDirection == WPopupDirection.TOP) {
                    triangle!!.setBackgroundResource(R.drawable.triangle_down)
                    commonRootLayout.addView(triangle, 1, params)
                } else {
                    triangle!!.setBackgroundResource(R.drawable.triangle_up)
                    commonRootLayout.addView(triangle, 0, params)
                }
            }
            oldDirection = showDirection
        } else {
            if (triangle != null) {
                // 就算显示位置相同也要每次都更改三角形的位置
                params = triangle!!.layoutParams as LinearLayout.LayoutParams
                params.leftMargin = margin
                triangle!!.layoutParams = params
            }
        }

        /**
         * 设置三角形的颜色
         */
        val layerList = triangle!!.background as LayerDrawable
        val rotate = layerList.getDrawable(0) as RotateDrawable
        (rotate.drawable as GradientDrawable).setColor(popParams.commonPopupBgColor)

        // 设置rv背景的颜色
        (recyclerView.background as GradientDrawable).setColor(popParams.commonPopupBgColor)


    }


    class Builder(activity: Activity) {

        companion object {
            const val VERTICAL = "VERTICAL"
            const val HORIZONTAL = "HORIZONTAL"
        }

        private val popParams = WPopParams(R.layout.pop_common, activity)

        /**
         * 设置显示的数据
         */
        fun setData(commonData: List<WPopupModel>): Builder {
            popParams.mCommonData = commonData
            return this
        }

        /**
         * 设置点击事件
         */
        fun setOnItemClickListener(itemClickListener: OnItemClickListener): Builder {
            popParams.mWItemClickListener = itemClickListener
            return this
        }

        /**
         * 设置popup的方向
         */
        fun setPopupOrientation(orientation: String): Builder {
            if (orientation == VERTICAL)
                popParams.commonPopupOrientation = LinearLayoutManager.VERTICAL
            else if (orientation == HORIZONTAL)
                popParams.commonPopupOrientation = LinearLayoutManager.HORIZONTAL

            return this
        }

        /**
         * 设置分割线的颜色 默认为白色
         */
        fun setDividerColor(color: Int): Builder {
            popParams.commonPopupDividerColor = color
            return this
        }

        /**
         * 设置分割线的粗细   默认为1
         */
        fun setDividerSize(size: Int): Builder {
            popParams.commonPopupDividerSize = size
            return this
        }

        /**
         * 设置分割线边距   默认为10
         */
        fun setDividerMargin(margin: Int): Builder {
            popParams.commonPopupDividerMargin = margin
            return this
        }

        /**
         * 设置是否背景半透明
         */
        fun setIsDim(isDim: Boolean): Builder {
            popParams.isDim = isDim
            return this
        }

        /**
         * 设置背景半透明的值  0.1f - 1f 值越大，越接近透明
         */
        fun setDimValue(dimValue: Float): Builder {
            popParams.dimValue = dimValue
            return this
        }

        /**
         * 设置背景
         */
        fun setPopupBgColor(color: Int): Builder {
            popParams.commonPopupBgColor = color
            return this
        }

        /**
         * 设置弹出时和view的距离
         */
        fun setPopupMargin(margin: Int): Builder {
            popParams.commonPopMargin = margin
            return this
        }

        /**
         * 获取长按事件的view
         */
        fun setClickView(view: View): Builder {
            popParams.longClickView = view
            return this
        }

        /**
         * 设置动画
         */
        fun setAnim(anim: Int): Builder {
            popParams.animRes = anim
            return this
        }

        /**
         * 设置icon的方向
         */
        fun setIconDirection(d: Int): Builder {
            popParams.commonIconDirection = d
            return this
        }

        /**
         * 设置点击取消
         */
        fun setCancelable(cancelable: Boolean): Builder {
            popParams.cancelable = cancelable
            return this
        }

        /**
         * 设置item字体颜色
         */
        fun setTextColor(color: Int): Builder {
            popParams.commonItemTextColor = color
            return this
        }

        /**
         * 设置item字体大小
         */
        fun setTextSize(size: Int): Builder {
            popParams.commonItemTextSize = size
            return this
        }

        fun setDrawablePadding(padding: Int): Builder{
            popParams.commonDraablePadding = padding
            return this
        }

        /**
         * 构建
         */
        fun create(): WPopup {
            return WPopup(popParams)
        }

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }
    }


}