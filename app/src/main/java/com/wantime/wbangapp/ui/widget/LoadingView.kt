package com.wantime.wbangapp.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wantime.wbangapp.utils.Constants
import java.util.*

/**
 *  on 2018/1/29.
 */
class LoadingView : View {


    private var proBarWith = 0
    private val paint = Paint()
    private val proBarNum = 4
    private var proBarMargin = 0
    private var choosePro = 0

    private val refreshProTime = 250
    private var xTimer: Timer? = null
    private var normalColor="#FFFFFF"

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        proBarWith = Constants.dip2px(context, 8.0f)
        proBarMargin = Constants.dip2px(context, 7.0f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startX =
            (width - ((proBarWith + proBarMargin) * proBarNum - proBarMargin)) / 2 + proBarWith / 2
        for (i in 0 until proBarNum) {
            paint.color =
                if (choosePro == i) Color.parseColor("#0088FF") else Color.parseColor(normalColor)
            canvas.drawCircle(
                (startX + i * (proBarWith + proBarMargin)).toFloat(),
                (height / 2).toFloat(),
                (proBarWith / 2).toFloat(),
                paint
            )
            paint.reset()
        }
    }

    fun start() {
        if (xTimer == null) {
            xTimer = Timer()
            xTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    choosePro++
                    if (choosePro >= proBarNum) choosePro = 0
                    postInvalidate()
                }
            }, 1, refreshProTime.toLong())
        }
    }

    fun stop() {
        if (xTimer != null) {
            xTimer!!.cancel()
        }
        xTimer = null
        choosePro = 0
    }

    fun setNormalColor(normalColor:String){
        this.normalColor=normalColor
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility) {
            VISIBLE -> start()
            else -> stop()
        }
    }

}
