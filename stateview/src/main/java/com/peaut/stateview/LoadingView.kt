package com.peaut.stateview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 *
 * @ProjectName:    StateView
 * @Package:        com.peaut.stateview
 * @ClassName:      LoadingView
 * @Description:
 * @Author:         peaut
 * @CreateDate:     2020-01-08 19:57
 * @UpdateUser:
 * @UpdateDate:     2020-01-08 19:57
 * @UpdateRemark:
 */

class LoadingView : View {

    companion object {
        private const val LINE_COUNT = 12
        private const val DEGREE_PER_LINE = 360 / LINE_COUNT
    }
    private var mSize: Int
    private var mPaintColor: Int
    private lateinit var mPaint: Paint
    private var mAnimateValue: Int = 0
    private var mAnimator: ValueAnimator? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0)
        mSize = typedArray.getDimensionPixelSize(R.styleable.LoadingView_loading_view_size, 32.toPx(context))
        mPaintColor = typedArray.getInt(R.styleable.LoadingView_android_color, Color.WHITE)
        typedArray.recycle()
        initPaint()
    }

    constructor(context: Context, color: Int, size: Int) : super(context) {
        mSize = size
        mPaintColor = color
        initPaint()
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint.color = mPaintColor
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND

    }

    fun setColor(color: Int){
        mPaintColor = color
        mPaint.color = mPaintColor
        invalidate()
    }

    fun setSize(size: Int){
        mSize = size
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mSize,mSize)
    }

    @SuppressLint("NewApi")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val saveLayer = canvas?.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        drawLoading(canvas,mAnimateValue * DEGREE_PER_LINE)
        canvas?.restoreToCount(saveLayer!!)
    }

    private fun drawLoading(canvas: Canvas?, value: Int) {
        val width = mSize / 12
        val height = mSize / 6
        mPaint.strokeWidth = width.toFloat()
        canvas?.rotate(value.toFloat(), (mSize / 2).toFloat(), (mSize / 2).toFloat())
        canvas?.translate((mSize / 2).toFloat(), (mSize / 2).toFloat())

        for (i in 0 until LINE_COUNT) {
            canvas?.rotate(DEGREE_PER_LINE.toFloat())
            mPaint.alpha = (255f * (i + 1) / LINE_COUNT).toInt()
            canvas?.translate(0f, (-mSize / 2 + width / 2).toFloat())
            canvas?.drawLine(0f, 0f, 0f, height.toFloat(), mPaint)
            canvas?.translate(0f, (mSize / 2 - width / 2).toFloat())
        }
    }

    private val mUpdateListener: ValueAnimator.AnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        mAnimateValue = it.animatedValue as Int
        invalidate()
    }

    fun start(){
        if (mAnimator == null){
            mAnimator = ValueAnimator.ofInt(0,LINE_COUNT - 1)
            mAnimator!!.addUpdateListener(mUpdateListener)
            mAnimator!!.duration = 600
            mAnimator!!.repeatMode = ValueAnimator.RESTART
            mAnimator!!.repeatCount = ValueAnimator.INFINITE
            mAnimator!!.interpolator = LinearInterpolator()
            mAnimator!!.start()
        }else if (!mAnimator!!.isStarted){
            mAnimator!!.start()
        }
    }

    private fun stop(){
        if (mAnimator != null){
            mAnimator!!.removeUpdateListener(mUpdateListener)
            mAnimator!!.removeAllUpdateListeners()
            mAnimator!!.cancel()
            mAnimator = null
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE){
            start()
        }else{
            stop()
        }
    }
}
