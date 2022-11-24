package com.zhangteng.svgalibrary.animation

import java.io.Serializable

/**
 * description: 文件实体
 * author: Swing
 * date: 2022/11/9
 */
class AnimationEntity : Serializable {
    /**
     * description: 动效文件校验码
     */
    var checkCode: String? = null

    /**
     * description: 动效文件路径
     */
    var animationUrl: String? = null
}