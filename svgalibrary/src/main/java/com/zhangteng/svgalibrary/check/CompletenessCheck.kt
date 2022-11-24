package com.zhangteng.svgalibrary.check

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.zhangteng.utils.MD5Util.getFileMD5
import com.zhangteng.utils.ThreadPoolUtils.Companion.instance
import java.io.File

/**
 * description: 动画完整性校验
 * author: Swing
 * date: 2022/11/8
 */
class CompletenessCheck(private val checkListener: CompletenessCheckListener?) {
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 0x123) {
                checkListener?.onCompleteness(true)
            } else if (msg.what == 0x124) {
                checkListener?.onCompleteness(false)
            }
        }
    }

    /**
     * description 校验文件是否完整
     *
     * @param filePath 文件路径
     * @param md5      文件md5
     */
    fun checkByHandler(filePath: String?, md5: String?) {
        instance.addExecuteTask {
            val fileMd5 = getFileMD5(filePath)
            val isCompleteness = TextUtils.equals(fileMd5, md5)
            if (isCompleteness) {
                mHandler.sendEmptyMessage(0x123)
            } else {
                mHandler.sendEmptyMessage(0x124)
            }
        }
    }

    companion object {
        /**
         * description 校验文件是否完整
         *
         * @param file 文件
         * @param md5  文件md5
         */
        fun check(file: File, md5: String?): Boolean {
            return if (file.exists()) {
                val fileMd5 = getFileMD5(file.absolutePath)
                TextUtils.equals(fileMd5, md5)
            } else {
                false
            }
        }

        /**
         * description 校验文件是否完整
         *
         * @param file          文件
         * @param md5           文件md5
         * @param checkListener 校验回调
         */
        fun check(file: File, md5: String?, checkListener: CompletenessCheckListener?) {
            if (file.exists()) {
                instance.addExecuteTask {
                    val fileMd5 = getFileMD5(file.absolutePath)
                    val isCompleteness = TextUtils.equals(fileMd5, md5)
                    checkListener?.onCompleteness(isCompleteness)
                }
            } else {
                checkListener?.onCompleteness(false)
            }
        }
    }
}