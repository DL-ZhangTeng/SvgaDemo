package com.zhangteng.svgalibrary.config

import java.io.Serializable

/**
 * description: 配置实体
 * author: Swing
 * date: 2022/11/9
 */
class ConfigEntity : Serializable {
    /**
     * description: 动效文件校验码
     */
    var checkCode: String? = null

    /**
     * description: 动效宽度
     */
    var animationWidth = 0f

    /**
     * description: 动效高度
     */
    var animationHeight = 0f

    /**
     * description: 动效中心X轴位置
     */
    var centerX = 0f

    /**
     * description: 动效中心Y轴位置
     */
    var centerY = 0f
}