package com.wanglu.lib

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

class WCommonAnim(private val view: View) {

    private val animSet: AnimatorSet = AnimatorSet()
    private var scaleXAnim: ObjectAnimator? = null
    private var scaleYAnim: ObjectAnimator? = null



    init {
        animSet.duration = 200
    }

    fun start() {
        if (animSet.isRunning)
            animSet.cancel()

        if (scaleXAnim == null) {
            scaleXAnim = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.2f, 1f)
            scaleYAnim = ObjectAnimator.ofFloat(view, View.SCALE_Y,  1.2f, 1f)
            animSet.playTogether(scaleXAnim, scaleYAnim)
        }
        animSet.start()
    }

}