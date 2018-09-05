package com.wanglu.lib.commonPop

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wanglu.lib.R

class CommonPopAdapter : RecyclerView.Adapter<CommonPopAdapter.ViewHolder>() {

    private var mData: List<CommonPopModel>? = null
    private var commonItemClickListener: CommonPopup.Builder.OnItemClickListener? = null

    fun setData(data: List<CommonPopModel>) {
        mData = data
        notifyDataSetChanged()
    }

    fun setItemClickListener(commonItemClickListener: CommonPopup.Builder.OnItemClickListener) {
        this.commonItemClickListener = commonItemClickListener
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

        holder.view.setOnClickListener {
            if (commonItemClickListener != null) {
                commonItemClickListener!!.onItemClick(holder.view, position)
            }
        }

        if (mData!![position].imgRes != -1) {

        }
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tv = view.findViewById<TextView>(R.id.mTvItem)
    }
}