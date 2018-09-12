package com.wanglu.lib

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup

data class WPopParams(
        val layoutRes: Int, // 布局
        val activity: Activity, // activity
        var isDim: Boolean = false,  // 是否半透明
        var dimValue: Float = 0.4f, // 半透明属性
        var cancelable: Boolean = true, // 点击外部可以dismiss
        var width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) {
    var mCommonData: List<WPopupModel>? = null    // common的数据
    var mWItemClickListener: WPopup.Builder.OnItemClickListener? = null
    var commonPopupOrientation: Int = LinearLayoutManager.VERTICAL // pop方向
    var commonPopupDividerColor = Color.WHITE   // 分割线的颜色
    var commonPopupDividerSize = 1  // 分割线的粗细
    var commonPopupDividerMargin = 10   // 分割线的margin
    var commonPopupBgColor = Color.parseColor("#A5000000")
    var commonItemTextColor = Color.parseColor("#ffffff")
    var commonItemTextSize = 14
    var commonPopMargin = 1
    var commonIconDirection = WPopupDirection.LEFT   // 传入的图片的位置
    var commonDraablePadding = 5
    var longClickView: View? = null   //长按点击事件的View
    var animRes = WPopupAnim.ANIM_ALPHA    // 动画
}