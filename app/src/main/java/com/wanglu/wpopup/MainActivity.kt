package com.wanglu.wpopup

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.wanglu.lib.*
import com.wanglu.wpopup.R.id.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val customPopup = BasePopup(
                WPopParams(
                        R.layout.view_custom,
                        this,
                        true,
                        cancelable = false,
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                )
        )
        val btnOJBK = customPopup.findViewById<Button>(R.id.selectSalaryBtn)
        btnOJBK.setOnClickListener { customPopup.dismiss() }

        tv6.setOnClickListener {

            customPopup.showAtDirection(WPopupDirection.BOTTOM)
        }

        val data = mutableListOf(WPopupModel("点赞"), WPopupModel("评论"), WPopupModel("吃香蕉"), WPopupModel("吃苹果"))
        val pop = WPopup.Builder(this)
                .setData(data)
                .setCancelable(false)
                .setPopupOrientation(WPopup.Builder.HORIZONTAL)
                .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Toast.makeText(view.context, data[position].text, Toast.LENGTH_LONG).show()
                    }
                })
                .create()

        val friendCircleData = mutableListOf(WPopupModel("点赞", R.mipmap.icon_support), WPopupModel("评论", R.mipmap.icon_comment))
        val friendCirclePop = WPopup.Builder(this)
                .setData(friendCircleData)
                .setAnim(WPopupAnim.ANIM_FRIEND_CIRCLE)
                .setIconDirection(WPopupDirection.LEFT)
                .setPopupOrientation(WPopup.Builder.HORIZONTAL)
                .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Toast.makeText(view.context, data[position].text, Toast.LENGTH_LONG).show()
                    }
                })
                .create()


        val dimPop = WPopup.Builder(this)
                .setData(data)
                .setTextColor(Color.WHITE)
                .setIsDim(true)
                .setPopupBgColor(Color.parseColor("#3498db"))
                .setDividerColor(Color.parseColor("#95a5a6"))
                .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Toast.makeText(view.context, data[position].text, Toast.LENGTH_LONG).show()
                    }
                })
                .create()


        val longData = mutableListOf(WPopupModel("点赞"), WPopupModel("评论"), WPopupModel("吃香蕉"), WPopupModel("吃蕉"), WPopupModel("香蕉"), WPopupModel("吃香蕉"), WPopupModel("香蕉"))

        val longClickPop = WPopup.Builder(this)
                .setData(longData)
                .setPopupOrientation(WPopup.Builder.VERTICAL)
                .setClickView(longClickView)
                .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Toast.makeText(view.context, "$position", Toast.LENGTH_LONG).show()
                    }
                })
                .create()


        longClickView.setOnLongClickListener {
            longClickPop.showAtFingerLocation()
            true
        }



        tvTitle.setOnClickListener {
            dimPop.showAtView(tvTitle)
        }

        ivMore.setOnClickListener {
            dimPop.showAtView(ivMore)
        }

        tv.setOnClickListener {
            pop.showAtView(tv)
        }

        tv2.setOnClickListener {
            pop.showAtView(tv2)
        }

        tv3.setOnClickListener {
            dimPop.showAtView(tv3)
        }

        tv4.setOnClickListener {
            dimPop.showAtView(tv4)
        }

        tv5.setOnClickListener {
            friendCirclePop.showAtDirectionByView(tv5, WPopupDirection.LEFT)
        }

    }
}
