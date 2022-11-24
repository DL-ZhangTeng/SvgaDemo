package com.zhangteng.svgalibrary.download

import com.zhangteng.utils.IException

/**
 * description: 下载回调
 * author: Swing
 * date: 2022/9/13
 */
interface DownloadCallBack {

    /**
     * 成功回调，有可能在子线程回调
     *
     * @param filePath      文件路径
     */
    fun onSuccess(filePath: String?)

    /**
     * 失败回调
     *
     * @param iException 错误信息
     */
    fun onFailure(iException: IException?)
}