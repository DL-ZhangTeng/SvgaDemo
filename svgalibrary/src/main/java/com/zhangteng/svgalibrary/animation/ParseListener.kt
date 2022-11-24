package com.zhangteng.svgalibrary.animation

import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity

interface ParseListener {
    fun onComplete(videoEntity: SVGAVideoEntity?) {}
    fun onComplete(drawable: SVGADrawable?)
    fun onError()
}