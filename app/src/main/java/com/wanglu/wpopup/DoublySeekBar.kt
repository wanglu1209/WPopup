package com.wanglu.wpopup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


/**
 * Created by WangLu on 2018/5/5.
 */
class DoublySeekBar(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private val mBgLinePaint: Paint = Paint()
    private val mFgLinePaint: Paint = Paint()
    private var mHandBitmap: Bitmap

    private var mBgHeight: Float
    private var mFgHeight: Float
    private var mBgLeftPadding: Float
    private var mBgRightPadding: Float
    private var mFgLeftPadding: Float
    private var mFgRightPadding: Float
    private var mHandLeftPadding: Float
    private var mHandRightPadding: Float

    private var leftOffset: Float
    private var rightOffset = 0f

    private var selectedHand = ""
    private var interval = 0
    private var lastX = 0f
    private var lastY = 0f
    private var mText: List<String>? = null

    private var mHeight: Float
    private var mWidth = 0f
    private var leftText: String = ""
    private var rightText: String = ""

    private var mTextChangeListener: OnTextChangedListener? = null
    private var value = Value("", "")

    init {
        mBgLinePaint.strokeJoin = Paint.Join.ROUND
        mBgLinePaint.isAntiAlias = true
        mFgLinePaint.isAntiAlias = true
        mHandBitmap = changeBitmapSize(R.drawable.hand, dp2px(context!!, 15f).toInt(), dp2px(context, 15f).toInt())
        mBgHeight = mHandBitmap.height + dp2px(context, 45f)    // 背景圆角矩形的高
        mFgHeight = mBgHeight - dp2px(context, 8f)

        mBgLeftPadding = dp2px(context, 20f)
        mBgRightPadding = dp2px(context, 20f)
        mHeight = mBgHeight + dp2px(context, 10f)   // 整个View的高度


        mFgLeftPadding = mBgLeftPadding + dp2px(context, 8f)     // 前景的边距
        mFgRightPadding = mBgRightPadding + dp2px(context, 8f)

        mHandLeftPadding = mFgLeftPadding + dp2px(context, 15f)     // 手拖动的边距
        mHandRightPadding = mFgRightPadding + dp2px(context, 15f) + mHandBitmap.width

        leftOffset = mFgLeftPadding
    }


    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val specMode: Int = View.MeasureSpec.getMode(widthMeasureSpec)
        var specSize = 0

        when (specMode) {
            View.MeasureSpec.EXACTLY, View.MeasureSpec.AT_MOST, View.MeasureSpec.UNSPECIFIED
            -> specSize = View.MeasureSpec.getSize(widthMeasureSpec)
        }
        mWidth = specSize.toFloat()
        rightOffset = mWidth - mFgRightPadding
        setMeasuredDimension(mWidth.toInt(), mHeight.toInt())
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mText != null) {
            interval = ((mWidth - mFgLeftPadding - mFgRightPadding) / mText!!.size).toInt()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 首先画背景圆角矩形
        mBgLinePaint.color = context!!.resources.getColor(R.color.colorPrimary)
        mBgLinePaint.strokeWidth = mBgHeight
        mBgLinePaint.style = Paint.Style.FILL

        canvas!!.drawRoundRect(mBgLeftPadding, mHeight - mBgHeight, mWidth - mBgRightPadding, mBgHeight, mBgHeight / 2, mBgHeight / 2, mBgLinePaint)

        // 设置矩形边框
        mBgLinePaint.color = context!!.resources.getColor(R.color.colorPrimaryDark)
        mBgLinePaint.strokeWidth = 5f
        mBgLinePaint.style = Paint.Style.STROKE
        canvas.drawRoundRect(mBgLeftPadding, mHeight - mBgHeight, mWidth - mBgRightPadding, mBgHeight, mBgHeight / 2, mBgHeight / 2, mBgLinePaint)


        // 画前景
        mFgLinePaint.color = Color.parseColor("#4cFFFFFF")
        mFgLinePaint.strokeWidth = mFgHeight
        mFgLinePaint.style = Paint.Style.FILL
        canvas.drawRoundRect(leftOffset, mHeight - mFgHeight, rightOffset, mFgHeight, mFgHeight / 2, mFgHeight / 2, mFgLinePaint)

        // 设置矩形边框
        mFgLinePaint.color = Color.parseColor("#7FFFFFFF")
        mFgLinePaint.strokeWidth = 5f
        mFgLinePaint.style = Paint.Style.STROKE
        canvas.drawRoundRect(leftOffset, mHeight - mFgHeight, rightOffset, mFgHeight, mFgHeight / 2, mFgHeight / 2, mFgLinePaint)

        canvas.drawBitmap(mHandBitmap, leftOffset + dp2px(context, 15f), mHeight / 2 - mHandBitmap.height / 2, mBgLinePaint)
        canvas.drawBitmap(mHandBitmap, rightOffset - dp2px(context, 15f) - mHandBitmap.width, mHeight / 2 - mHandBitmap.height / 2, mBgLinePaint)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event.y
        parent.requestDisallowInterceptTouchEvent(true)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                selectedHand = collide(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = x - lastX
                if (selectedHand == SELECTED_LEFT) {
                    leftOffset += offsetX.toInt()
                    if (leftOffset >= rightOffset - interval * 5) leftOffset = rightOffset - interval * 5
                    if (leftOffset < mFgLeftPadding) leftOffset = mFgLeftPadding
                }

                if (selectedHand == SELECTED_RIGHT) {
                    rightOffset += offsetX.toInt()
                    if (rightOffset > mWidth - mFgRightPadding) rightOffset = mWidth - mFgRightPadding
                    if (rightOffset <= leftOffset + interval * 5) rightOffset = leftOffset + interval * 5
                    if (rightOffset < mFgRightPadding) rightOffset = mFgRightPadding
                }


                if (mText != null && mText!!.isNotEmpty()) {
                    leftText = mText!![((leftOffset - mFgLeftPadding) / interval).toInt()]
                    rightText = mText!![if (((rightOffset - mFgLeftPadding) / interval) > mText!!.size - 1) mText!!.size - 1 else ((rightOffset - mFgLeftPadding) / interval).toInt()]

                    if (mTextChangeListener != null) {
                        value.left = leftText
                        value.right = rightText
                        mTextChangeListener!!.onTextChange(value)
                    }
                }

                invalidate()

                lastX = x
            }
        }
        return true
    }

    fun setText(text: List<String>){
        mText = text
        postInvalidate()
    }


    /**
     * 判断点击的是否是两个其中的一个， L左边 R右边
     */
    private fun collide(event: MotionEvent): String {
        if (event.x > leftOffset && event.x < leftOffset + mHandBitmap.width + dp2px(context, 35f)) {
            return SELECTED_LEFT
        }

        if (event.x > rightOffset - mHandRightPadding && event.x < rightOffset + mHandBitmap.width / 2) {
            return SELECTED_RIGHT
        }

        return ""
    }

    companion object {
        private val SELECTED_LEFT = "L"
        private val SELECTED_RIGHT = "R"
    }

    interface OnTextChangedListener {
        fun onTextChange(value: Value)
    }

    fun setOnTextChangedListener(l: OnTextChangedListener){
        mTextChangeListener = l
    }

    data class Value(var left: String, var right: String)

    private fun dp2px(context: Context, dpValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics)
    }

    private fun sp2px(context: Context, spValue: Float): Int {
        return (spValue * context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    private fun px2sp(context: Context, pxValue: Float): Int {
        return (pxValue / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()
    }

    private fun changeBitmapSize(res: Int, newWidth: Int, newHeight: Int): Bitmap {
        var bitmap = BitmapFactory.decodeResource(resources, res)
        val width = bitmap.width
        val height = bitmap.height

        //计算压缩的比率
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        //获取想要缩放的matrix
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        //获取新的bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        bitmap.width
        bitmap.height
        return bitmap
    }
}