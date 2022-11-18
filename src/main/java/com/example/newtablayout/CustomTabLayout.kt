package com.example.newtablayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.icu.text.CaseMap.Title
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.newtablayout.util.DisPlayUtil
import kotlin.properties.Delegates

class CustomTabLayout : HorizontalScrollView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    private lateinit var viewPager2: ViewPager2
    private val mTabsContainer: LinearLayout = LinearLayout(context)
    private var mTabTextSizeSelect = 0
    private var mTabTextSizeUnSellect = 0
    private var mTabTextColorSelect = Color.BLACK
    private var mTabTextColorUnSelect = Color.GRAY
    private var mIndicatorBounds: Rect = Rect()
    private var mTabWidth = 0f
    private var mTabHeight = 0f
    private var mIndicatorLeft = 0
    private var mIndicatorRight = 0
    private var mIndicatorTop = 0
    private var mIndicatorBottom = 0
    private var mIndicatorDrawable = GradientDrawable()
    private var mIndicatorHeight = 0
    private var oldTabText: TextView? = null
    private var oldTabView: View? = null
    private var oldPosition = 0


    private fun initView(context: Context?, attrs: AttributeSet?) {
        if (context != null && attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomTabLayout)
            mTabTextSizeSelect = DisPlayUtil.px2sp(context,ta.getDimension(R.styleable.CustomTabLayout_tab_textSize_select, 20f))
            mTabTextSizeUnSellect = DisPlayUtil.px2sp(context,  ta.getDimension(R.styleable.CustomTabLayout_tab_textSize_unselect, 15f))
            mTabWidth = ta.getDimension(R.styleable.CustomTabLayout_tab_width, 160f)
            mTabHeight = ta.getDimension(R.styleable.CustomTabLayout_tab_height, 120f)
            mIndicatorHeight = DisPlayUtil.dip2px(context,ta.getDimension(R.styleable.CustomTabLayout_indicator_height,6f))
            mTabTextColorSelect =
                ta.getColor(R.styleable.CustomTabLayout_tab_textColor_select, Color.BLACK)
            mTabTextColorUnSelect =
                ta.getColor(R.styleable.CustomTabLayout_tab_textColor_unselect, Color.GRAY)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, mIndicatorHeight)
            mTabsContainer.orientation = LinearLayout.HORIZONTAL
            mTabsContainer.gravity = Gravity.CENTER
            mTabsContainer.layoutParams = params
            addView(mTabsContainer)
        }
    }


    fun setAdapterAndTitle(viewPager2: ViewPager2, titles: Array<String>) {
        this.viewPager2 = viewPager2
        viewPager2.adapter?.let {
            for (index in 0 until it.itemCount) {
                val tabView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_tabs, mTabsContainer, false)
                tabView.layoutParams.width = mTabWidth.toInt()
                tabView.layoutParams.height = mTabHeight.toInt()
                val tabText = tabView.findViewById<TextView>(R.id.tv_tabs)
                tabText.apply {
                    setTextColor(mTabTextColorUnSelect)
                    textSize = mTabTextSizeUnSellect.toFloat()
                    text = titles.getOrElse(index) {
                        ""
                    }
                }
                tabView.setOnClickListener {
                    viewPager2.currentItem = mTabsContainer.indexOfChild(tabView)
                }
                mTabsContainer.addView(tabView)
            }
        }
        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffset == 0f) oldPosition = position
                calculateIndicatorBounds(position, positionOffset, position < oldPosition)
                invalidate()
            }
        })
    }

    private fun calculateIndicatorBounds(
        position: Int,
        positonOffSet: Float,
        isleft: Boolean
    ) {
        if (mTabsContainer.childCount == 0) return
        val tabView = mTabsContainer[position]
        val tabText = tabView.findViewById<TextView>(R.id.tv_tabs)
        if (positonOffSet == 0f) {
            oldTabText?.apply {
                setTextColor(mTabTextColorUnSelect)
                textSize =mTabTextSizeUnSellect.toFloat()
            }
            tabText.apply {
                setTextColor(mTabTextColorSelect)
                textSize = mTabTextSizeSelect.toFloat()
            }
            oldTabText = tabText
        }
        val width = tabView.width
        val offwidth =
            (width * if (isleft) (1 - positonOffSet) else positonOffSet).toInt() + if (isleft) (oldPosition - position - 1) * width else (position - oldPosition)*width
        if (positonOffSet == 0f) {
            oldTabView = tabView
        }
        mIndicatorTop = oldTabView!!.bottom
        mIndicatorBottom = oldTabView!!.bottom + mIndicatorHeight
        mIndicatorLeft = if (isleft) oldTabView!!.left - offwidth else oldTabView!!.left
        mIndicatorRight = if (isleft) oldTabView!!.right else oldTabView!!.right + offwidth
        mIndicatorBounds.apply {
            this.left = mIndicatorLeft
            this.right = mIndicatorRight
            this.top = mIndicatorTop
            this.bottom = mIndicatorBottom
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null || mTabsContainer.childCount <= 0) return
        mIndicatorDrawable.bounds = mIndicatorBounds
        mIndicatorDrawable.setColor(Color.parseColor("#000000"))
        mIndicatorDrawable.draw(canvas)
    }
}