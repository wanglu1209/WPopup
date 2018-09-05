package com.wanglu.lib

import android.view.View

interface BaseBuilder<T>{
    fun setContentView(view: View): T
    fun setDimValue(dimValue: Float) : T
    fun setIsBgDim(boolean: Boolean) : T
}