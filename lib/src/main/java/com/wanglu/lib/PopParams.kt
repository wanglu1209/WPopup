package com.wanglu.lib

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import com.wanglu.lib.commonPop.CommonPopModel
import com.wanglu.lib.commonPop.CommonPopup

data class PopParams(
        val layoutRes: Int, // 布局
        val activity: Activity, // activity
        var isDim: Boolean = false,  // 是否半透明
        var dimValue: Float = 0.4f // 半透明属性
) {
    var commonData: List<CommonPopModel>? = null    // common的数据
    var commonItemClickListener: CommonPopup.Builder.OnItemClickListener? = null
    var commonPopupOrientation: Int = LinearLayoutManager.VERTICAL // pop方向
    var commonPopupDividerColor = Color.WHITE   // 分割线的颜色
    var commonPopupDividerSize = 1  // 分割线的粗细
    var commonPopupDividerMargin = 10   // 分割线的margin
    var commonPopupBgColor = Color.parseColor("#77000000")
}