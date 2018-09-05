package com.wanglu.wpopup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.wanglu.lib.commonPop.CommonPopModel
import com.wanglu.lib.commonPop.CommonPopup
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val data = mutableListOf(CommonPopModel("点赞"), CommonPopModel("评论"), CommonPopModel("吃香蕉"))
        val pop = CommonPopup.Builder(this)
                .setData(data)
                .setIsDim(true)
                .setPopupOrientation(CommonPopup.Builder.HORIZONTAL)
                .setOnItemClickListener(object : CommonPopup.Builder.OnItemClickListener{
                    override fun onItemClick(view: View, position: Int) {
                        Toast.makeText(view.context, "$position", Toast.LENGTH_LONG).show()
                    }
                })
                .create()

        tv.setOnClickListener {
            pop.showAsDropDown(tv)
        }

    }
}
