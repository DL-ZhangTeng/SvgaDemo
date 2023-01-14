package com.zhangteng.svgalibrary.animation

import com.opensource.svgaplayer.SVGAVideoEntity

interface ParseListener {
    fun onComplete(videoEntity: SVGAVideoEntity?) {}
    fun onError() {}
}