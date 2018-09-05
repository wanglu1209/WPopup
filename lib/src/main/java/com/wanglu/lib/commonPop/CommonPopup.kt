package com.wanglu.lib.commonPop

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.wanglu.lib.BasePopup
import com.wanglu.lib.PopParams
import com.wanglu.lib.R
import com.wanglu.lib.rvDivider.HorizontalDividerItemDecoration
import com.wanglu.lib.rvDivider.VerticalDividerItemDecoration


class CommonPopup(popParams: PopParams) : BasePopup(popParams) {

    private var commonAdapter: CommonPopAdapter = CommonPopAdapter()
    private var recyclerView: RecyclerView

    init {
        commonAdapter.setData(popParams.commonData!!)
        commonAdapter.setItemClickListener(popParams.commonItemClickListener!!)
        recyclerView = getContentView().findViewById(R.id.mRvCommon)
        recyclerView.adapter = commonAdapter
        recyclerView.layoutManager = LinearLayoutManager(getContext(), popParams.commonPopupOrientation!!, false)
        when(popParams.commonPopupOrientation){
            LinearLayoutManager.HORIZONTAL ->
                recyclerView.addItemDecoration(VerticalDividerItemDecoration.Builder(getContext()).margin(popParams.commonPopupDividerMargin).size(popParams.commonPopupDividerSize).color(popParams.commonPopupDividerColor).build())
            LinearLayoutManager.VERTICAL ->
                recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(getContext()).margin(popParams.commonPopupDividerMargin).size(popParams.commonPopupDividerSize).color(popParams.commonPopupDividerColor).build())
        }
    }

    override fun show(view: View) {

        super.show(view)
    }


    class Builder(activity: Activity) {

        companion object {
            const val VERTICAL = "VERTICAL"
            const val HORIZONTAL = "HORIZONTAL"
        }

        private val popParams = PopParams(R.layout.pop_common, activity)

        /**
         * 设置显示的数据
         */
        fun setData(commonData: List<CommonPopModel>): Builder {
            popParams.commonData = commonData
            return this
        }

        /**
         * 设置点击事件
         */
        fun setOnItemClickListener(itemClickListener: OnItemClickListener): Builder {
            popParams.commonItemClickListener = itemClickListener
            return this
        }

        /**
         * 设置popup的方向
         */
        fun setPopupOrientation(orientation: String) : Builder{
            if(orientation == VERTICAL)
                popParams.commonPopupOrientation = LinearLayoutManager.VERTICAL
            else if (orientation == HORIZONTAL)
                popParams.commonPopupOrientation = LinearLayoutManager.HORIZONTAL

            return this
        }

        /**
         * 设置分割线的颜色   默认为白色
         */
        fun setDividerColor(color: Int): Builder{
            popParams.commonPopupDividerColor = color
            return this
        }

        /**
         * 设置分割线的粗细   默认为1
         */
        fun setDividerSize(size: Int): Builder{
            popParams.commonPopupDividerSize = size
            return this
        }

        /**
         * 设置分割线边距   默认为10
         */
        fun setDividerMargin(margin: Int): Builder{
            popParams.commonPopupDividerMargin = margin
            return this
        }

        /**
         * 设置是否背景半透明
         */
        fun setIsDim(isDim: Boolean): Builder{
            popParams.isDim = isDim
            return this
        }

        /**
         * 设置背景半透明的值  0.1f - 1f
         */
        fun setDimValue(dimValue: Float): Builder{
            popParams.dimValue = dimValue
            return this
        }

        /**
         * 设置背景
         */
        fun setPopupBgColor(color: Int): Builder{
            popParams.commonPopupBgColor = color
            return this
        }

        /**
         * 构建
         */
        fun create(): CommonPopup {
            return CommonPopup(popParams)
        }

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }
    }
}