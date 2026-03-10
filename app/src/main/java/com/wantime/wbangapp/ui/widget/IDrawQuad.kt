package com.wantime.wbangapp.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable
import kotlin.properties.Delegates


class IDrawQuad : View {
    private var paint: Paint by Delegates.notNull()
    private var path: Path  by Delegates.notNull()
    private var defaultColor = Color.parseColor("#3a509b")
    private var mRate=0.5f

    constructor(context: Context?) : super(context) {
        initData()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        initData()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initData()
    }


    private fun initData() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL //设置为不实心
        path = Path()
        paint.color =defaultColor
    }

    override fun onDraw(canvas: Canvas) {
        onDrawFrontRect(canvas)
        onDrawQuad(canvas)
        super.onDraw(canvas)
//        val x1 = 200
//        val y1 = 200
//        val x3 = 400
//        val y3 = 200
//        val x2 = (x1 + x3) / 2
//        val y2 = y1 + 30
//        path!!.moveTo(x1.toFloat(), y1.toFloat()) //起点
//        path!!.quadTo(x2.toFloat(), y2.toFloat(), x3.toFloat(), y3.toFloat()) //3点画弧
//        val xa1 = 150
//        val ya1 = 400
//        val xa3 = 450
//        val ya3 = 400
//        val xa2 = (xa1 + xa3) / 2
//        val ya2 = 460
//        path!!.moveTo(xa1.toFloat(), ya1.toFloat())
//        path!!.quadTo(xa2.toFloat(), ya2.toFloat(), xa3.toFloat(), ya3.toFloat())
//        path!!.moveTo(x1.toFloat(), y1.toFloat())
//        path!!.lineTo(xa1.toFloat(), ya1.toFloat())
//        path!!.moveTo(x3.toFloat(), y3.toFloat())
//        path!!.lineTo(xa3.toFloat(), ya3.toFloat())
//        canvas.drawPath(path!!, paint!!)
    }

    private fun onDrawFrontRect(canvas: Canvas) {
        val mRect = Rect(0, 0, width, (height * mRate).toInt())
        canvas.drawRect(mRect,paint)
    }

    private fun onDrawQuad(canvas: Canvas){
        path.moveTo(0f,(height * mRate))
        path.quadTo((width/2).toFloat(),height.toFloat(),width.toFloat(),(height * mRate).toFloat())
        canvas.drawPath(path, paint)
    }
}