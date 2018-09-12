package com.wanglu.lib

import android.graphics.Color
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

    fun setData(data: List<WPopupModel>) {
        mData = data
        notifyDataSetChanged()
    }

    fun setDirection(d: Int) {
        direction = d
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
                popup.dismiss()
                mWItemClickListener!!.onItemClick(holder.view, position)
            }
        }

        if (mData!![position].imgRes != -1) {
            val drawable = popup.getContext().resources.getDrawable(mData!![position].imgRes)
            when (direction) {
                WPopupDirection.BOTTOM -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
                WPopupDirection.TOP -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
                WPopupDirection.LEFT -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                WPopupDirection.RIGHT -> holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            }
            holder.tv.compoundDrawablePadding = Utils.dp2px(holder.tv.context, drawablePadding)
        }
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tv = view.findViewById<TextView>(R.id.mTvItem)
    }
}