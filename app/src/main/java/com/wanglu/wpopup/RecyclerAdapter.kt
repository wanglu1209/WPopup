package com.wanglu.wpopup

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class RecyclerAdapter(layoutResId: Int) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId) {
    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper!!.addOnLongClickListener(R.id.mTvItem)
    }
}