package com.example.newtablayout.util

import android.content.Context

object DisPlayUtil {
    fun px2dip(context: Context, pxValue: Float): Int =
        (pxValue / (context.resources.displayMetrics.density) + 0.5f).toInt()

    fun dip2px(context: Context, dipValue: Float): Int =
        (dipValue * (context.resources.displayMetrics.density) + 0.5f).toInt()

    fun px2sp(context: Context, pxValue: Float): Int =
        (pxValue / (context.resources.displayMetrics.scaledDensity) + 0.5f).toInt()

    fun sp2px(context: Context, spValue: Float): Int =
        (spValue * (context.resources.displayMetrics.scaledDensity) + 0.5f).toInt()
}