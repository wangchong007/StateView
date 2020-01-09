package com.peaut.stateview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ScrollingView
import java.lang.IllegalArgumentException

/**
 *
 * @ProjectName:    SateView
 * @Package:        com.peaut.stateview
 * @ClassName:      StateView
 * @Description:
 * @Author:         peaut
 * @CreateDate:     2020-01-08 19:50
 * @UpdateUser:
 * @UpdateDate:     2020-01-08 19:50
 * @UpdateRemark:
 */

class StateView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    //empty layout resource id
    private var mEmptyResource: Int = R.layout.base_empty

    //retry layout resource id
    private var mRetryResource: Int = R.layout.base_retry

    //loading layout resource id
    private var mLoadingResource: Int = R.layout.base_loading

    //emptyView
    private var mEmptyView: View? = null
    //retryView
    private var mRetryView: View? = null
    //loadingView
    private var mLoadingView: View? = null

    var inflater: LayoutInflater? = null
    private var mRetryClickListener: (() -> Unit) ?= null
    private var currentState = 0

    //layoutParams
    private var mLayoutParamConstrain: ConstraintLayout.LayoutParams? = null

    init {
        mLayoutParamConstrain = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        visibility = GONE
        setWillNotDraw(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(0,0)
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas?) {

    }

    override fun dispatchDraw(canvas: Canvas?) {
    }

    override fun setVisibility(visibility: Int) {
        setVisibility(mEmptyView,visibility)
        setVisibility(mRetryView,visibility)
        setVisibility(mLoadingView,visibility)
    }

    private fun setVisibility(view: View?, visibility: Int) {
        if (visibility != view?.visibility) {
            view?.visibility = visibility
        }
    }

    //show content view
    fun showContent() {
        currentState = STATE_CONTENT
        //show all views,while hide self
        showAllViews()
    }

    fun showEmpty(): View {
        currentState = STATE_EMPTY
        if (mEmptyView == null) {
            mEmptyView = inflate(mEmptyResource)
        }
        //hide all views,while show self
        hideAllViews()
        showView(mEmptyView!!)
        return mEmptyView!!
    }

    fun showRetry(): View {
        currentState = STATE_ERROR
        if (mRetryView == null) {
            mRetryView = inflate(mRetryResource)
            mRetryView!!.singleClick {
                mRetryClickListener?.invoke()
            }
        }
        hideAllViews()
        showView(mRetryView!!)
        return mRetryView!!
    }

    fun showLoading(): View {
        currentState = STATE_LOADING
        if (mLoadingView == null) {
            mLoadingView = inflate(mLoadingResource)
        }
        hideAllViews()
        showView(mLoadingView!!)
        return mLoadingView!!
    }

    private fun showView(view: View) {
        setVisibility(view, VISIBLE)
        hideViews(view)
    }

    private fun hideViews(showView: View) {
        when {
            mEmptyView === showView -> {
                setVisibility(mLoadingView, GONE)
                setVisibility(mRetryView, GONE)
            }
            mRetryView === showView -> {
                setVisibility(mEmptyView, GONE)
                setVisibility(mLoadingView, GONE)
            }
            else -> {
                setVisibility(mRetryView, GONE)
                setVisibility(mEmptyView, GONE)
            }
        }
    }

    private fun showAllViews() {
        val parent = parent as ViewGroup
        for (index in 0 until parent.childCount) {
            parent.getChildAt(index).visibility = VISIBLE
        }
        visibility = GONE
    }

    private fun hideAllViews() {
        val parent = parent as ViewGroup
        for (index in 0 until parent.childCount) {
            parent.getChildAt(index).visibility = GONE
        }
        visibility = VISIBLE
    }

    private fun inflate(@LayoutRes layoutRes: Int): View {
        val parentView = parent ?: throw IllegalArgumentException("StateView don't have a parent view")

        val factory = inflater ?: LayoutInflater.from(context)

        val view = factory.inflate(layoutRes,parentView as ViewGroup,false)
        val index = parentView.indexOfChild(this)

        view.isClickable = true
        view.visibility = GONE
        val layoutParams = layoutParams
        if (layoutParams != null) {
            when(parentView){
                is ConstraintLayout -> parentView.addView(view,index,mLayoutParamConstrain)
                else -> parentView.addView(view,index,layoutParams)
            }
        }else {
            parentView.addView(view,index)
        }

        if (mLoadingView != null && mEmptyView != null && mRetryView != null) {
            parentView.removeViewInLayout(this)
        }
        return view
    }

    fun setEmptyResource(@LayoutRes emptyResource: Int) {
        this.mEmptyResource = emptyResource
    }

    fun setRetryResource(@LayoutRes retryResource: Int) {
        this.mRetryResource = retryResource
    }

    fun setLoadingResource(@LayoutRes loadingResource: Int) {
        this.mLoadingResource = loadingResource
    }

    fun setOnRetryClickListener(listener: () -> Unit) {
        this.mRetryClickListener = listener
    }

    fun restoreContent() {
        if (currentState == STATE_ERROR) {
            showContent()
        }
    }

    companion object{
        const val STATE_ERROR = 0x00
        const val STATE_LOADING = 0x01
        const val STATE_CONTENT = 0x02
        const val STATE_EMPTY = 0x03

        fun inject(parentView: ViewGroup): StateView{
            var parent = parentView
            if (parent is LinearLayout || parent is ScrollView || parent is AdapterView<*> ||
                parent is ScrollingView && parent is NestedScrollingChild ||
                parent is NestedScrollingParent && parent is NestedScrollingChild
            ) {
                val viewParent = parent.parent
                if (viewParent == null) {
                    throw IllegalArgumentException("the viewGroup need a parent view")
                }else {
                    val root = FrameLayout(parent.context)
                    root.layoutParams = parent.layoutParams
                    if (viewParent is ViewGroup) {
                        viewParent.removeView(parent)
                        viewParent.addView(root)
                    }
                    val layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    parent.layoutParams = layoutParams
                    root.addView(parent)
                    parent = root
                }
            }
            val stateView = StateView(parent.context)
            parent.addView(stateView)
            stateView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            stateView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            return stateView
        }
    }

}