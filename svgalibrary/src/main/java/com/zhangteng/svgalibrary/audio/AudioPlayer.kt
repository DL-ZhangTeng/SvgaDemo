package com.zhangteng.svgalibrary.audio

import com.opensource.svgaplayer.SVGASoundManager
import com.opensource.svgaplayer.SVGAVideoEntity

/**
 * description: 音频播放
 * author: Swing
 * date: 2022/11/8
 */
object AudioPlayer {

    /**
     * description 初始化
     *
     */
    fun init() {
        SVGASoundManager.init()
    }

    /**
     * description 根据当前播放实体，设置音量
     *
     * @param volume - 范围在 0, 1
     * @param entity - 根据需要控制对应 entity 音量大小，若为空则控制所有正在播放的音频音量
     */
    fun setVolume(volume: Float?, entity: SVGAVideoEntity?) {
        SVGASoundManager.setVolume(volume!!, entity)
    }

    /**
     * description 释放音频资源
     */
    fun release() {
        SVGASoundManager.release()
    }
}