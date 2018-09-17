package com.wanglu.lib

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WPopupAdapter(private val popup: WPopup) : RecyclerView.Adapter<WPopupAdapter.ViewHolder>() {

    private var mData: List<WPopupModel>? = null
    private var mWItemClickListener: WPopup.Builder.OnItemClickListener? = null
    private var direction: Int? = null
    private var textColor = Color.parseColor("#ffffff")
    private var textSize = 14
    private var drawablePadding = 10
    private var key = 0
    private val views = hashMapOf<Int, Int>()
    private var isAnim: Boolean = false

    fun setData(data: List<WPopupModel>) {
        mData = data
        notifyDataSetChanged()
    }

    fun setDirection(d: Int) {
        direction = d
        notifyDataSetChanged()
    }

    // 普通在一个页面上使用相同按钮
    fun setShowView(view: View) {
        key = view.hashCode()
        if (!views.keys.contains(key)) {
            views[key] = 0
        }
        notifyDataSetChanged()
    }

    // 在rv/lv中使用相同的按钮，必须传入position，不然item复用view的hashcode也会重复
    fun setShowView(position: Int) {

        key = position
        if (!views.keys.contains(key)) {
            views[key] = 0
        }
        notifyDataSetChanged()
    }


    fun setTextColor(color: Int) {
        textColor = color
        notifyDataSetChanged()
    }

    fun setTextSize(size: Int) {
        this.textSize = size
        notifyDataSetChanged()
    }

    fun setDrawablePadding(padding: Int) {
        this.drawablePadding = padding
        notifyDataSetChanged()
    }

    fun setIsEnableChangeAnim(isEnable: Boolean) {
        isAnim = isEnable
    }

    fun setItemClickListener(wItemClickListener: WPopup.Builder.OnItemClickListener) {
        this.mWItemClickListener = wItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_common_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = mData!![position].text
        holder.tv.setTextColor(textColor)
        holder.tv.textSize = textSize.toFloat()

        holder.view.setOnClickListener {
            if (mWItemClickListener != null) {
                // 点击之后判断如果有更换的图片，则替换
                if (mData!![position].imgRes != -1 && mData!![position].switchImgRes != -1) {
                    val drawable: Drawable
                    val text: String
                    if (views[key] == 0) {
                        drawable = popup.getContext().resources.getDrawable(mData!![position].switchImgRes)
                        text = if (mData!![position].switchText.isNotEmpty())
                            mData!![position].switchText
                        else
                            mData!![position].text
                        views[key] = 1
                    } else {
                        drawable = popup.getContext().resources.getDrawable(mData!![position].imgRes)
                        text = mData!![position].text
                        views[key] = 0
                    }
                    if (isAnim)
                        WCommonAnim(holder.tv).start()
                    holder.tv.text = text
                    showImg(holder, drawable)
                }


                val handler = @SuppressLint("HandlerLeak")
                object : Handler() {
                    override fun handleMessage(msg: Message?) {
                        super.handleMessage(msg)
                        popup.dismiss()
                    }
                }
                handler.sendEmptyMessageDelayed(1, 220)
                mWItemClickListener!!.onItemClick(holder.view, position)
            }
        }

        // 如果可以切换的text不是空，那么则判断是否切换过
        if (mData!![position].switchText.isNotEmpty()) {
            val text: String = if (views[key] == 0) {
                mData!![position].text
            } else {
                mData!![position].switchText
            }
            holder.tv.text = text
        } else {
            holder.tv.text = mData!![position].text
        }


        // 如果图片资源不是空
        if (mData!![position].imgRes != -1) {
            val drawable: Drawable = if (views[key] == 0) {
                popup.getContext().resources.getDrawable(mData!![position].imgRes)
            } else {
                if (mData!![position].switchImgRes != -1)
                    popup.getContext().resources.getDrawable(mData!![position].switchImgRes)
                else
                    popup.getContext().resources.getDrawable(mData!![position].imgRes)
            }
            showImg(holder, drawable)
            holder.tv.compoundDrawablePadding = Utils.dp2px(holder.tv.context, drawablePadding)
        }

    }

    private fun showImg(holder: ViewHolder, drawable: Drawable) {

        when (direction) {
            WPopupDirection.BOTTOM -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
            WPopupDirection.TOP -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            WPopupDirection.LEFT -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            WPopupDirection.RIGHT -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tv = view.findViewById<TextView>(R.id.mTvItem)
    }
}