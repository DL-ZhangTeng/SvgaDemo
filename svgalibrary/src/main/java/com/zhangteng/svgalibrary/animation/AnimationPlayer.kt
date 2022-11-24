package com.zhangteng.svgalibrary.animation

import android.annotation.SuppressLint
import android.content.Context
import android.net.http.HttpResponseCache
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAParser.Companion.shareParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.utils.log.SVGALogger.setLogEnabled
import com.zhangteng.utils.MD5Util.md5Decode32
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/**
 * description: 动画播放
 * author: Swing
 * date: 2022/11/8
 */
object AnimationPlayer {
    @SuppressLint("StaticFieldLeak")
    private var svgaParser: SVGAParser? = null

    /**
     * description 初始化
     *
     * @param context 上下文
     */
    fun init(context: Context, isLogEnabled: Boolean = true) {
        try {
            val cacheDir = File(context.applicationContext.cacheDir, "AnimationPlayer")
            HttpResponseCache.install(cacheDir, (1024 * 1024 * 256).toLong())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        setLogEnabled(isLogEnabled)
        svgaParser = shareParser()
        svgaParser?.init(context)
    }

    /**
     * description 设置动画宽高
     *
     * @param frameWidth  宽
     * @param frameHeight 高
     */
    fun setFrameSize(frameWidth: Int, frameHeight: Int) {
        svgaParser?.setFrameSize(frameWidth, frameHeight)
    }

    /**
     * description 解析Assets资源
     *
     * @param animationName 资源名
     * @param parseListener 解析回调
     */
    fun decodeFromAssets(animationName: String?, parseListener: ParseListener?) {
        svgaParser?.decodeFromAssets(animationName!!, object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                parseListener?.onComplete(videoItem)
                parseListener?.onComplete(SVGADrawable(videoItem))
            }

            override fun onError() {
                parseListener?.onError()
            }
        }, null)
    }

    /**
     * description 解网络资源
     *
     * @param animationPath 资源URL
     * @param parseListener 解析回调
     */
    fun decodeFromURL(animationPath: String?, parseListener: ParseListener?) {
        try {
            svgaParser?.decodeFromURL(URL(animationPath), object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    parseListener?.onComplete(videoItem)
                    parseListener?.onComplete(SVGADrawable(videoItem))
                }

                override fun onError() {
                    parseListener?.onError()
                }
            }, null)
        } catch (e: MalformedURLException) {
            parseListener!!.onError()
        }
    }

    /**
     * description 加载Assets动画
     *
     * @param animationView 动画view
     * @param animationName 资源名
     * @param animationView 解析回调
     */
    fun loadAnimationFromAssets(
        animationView: SVGAImageView,
        animationName: String?,
        parseListener: ParseListener?
    ) {
        svgaParser?.decodeFromAssets(animationName!!, object : SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                animationView.setVideoItem(videoItem)
                parseListener?.onComplete(videoItem)
                parseListener?.onComplete(animationView.drawable as SVGADrawable)
                animationView.stepToFrame(0, true)
            }

            override fun onError() {
                parseListener?.onError()
            }
        }, null)
    }

    /**
     * description 加载网络动画
     *
     * @param animationView 动画view
     * @param animationPath 资源名
     * @param animationView 解析回调
     */
    fun loadAnimationFromURL(
        animationView: SVGAImageView?,
        animationPath: String?,
        parseListener: ParseListener?
    ) {
        try {
            svgaParser?.decodeFromURL(URL(animationPath), object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    animationView!!.setVideoItem(videoItem)
                    parseListener?.onComplete(videoItem)
                    parseListener?.onComplete(animationView.drawable as SVGADrawable)
                    animationView.stepToFrame(0, true)
                }

                override fun onError() {
                    parseListener?.onError()
                }
            }, null)
        } catch (e: MalformedURLException) {
            parseListener?.onError()
        }
    }

    /**
     * description 加载本地动画
     *
     * @param animationView 动画view
     * @param animationPath 资源路径
     * @param animationView 解析回调
     */
    fun loadAnimationFromPath(
        animationView: SVGAImageView?,
        animationPath: String?,
        parseListener: ParseListener?
    ) {
        try {
            val fileInputStream = FileInputStream(animationPath)
            val cacheKey = md5Decode32(animationPath!!)
            svgaParser?.decodeFromInputStream(
                fileInputStream,
                cacheKey,
                object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        animationView!!.setVideoItem(videoItem)
                        parseListener?.onComplete(videoItem)
                        parseListener?.onComplete(animationView.drawable as SVGADrawable)
                        animationView.stepToFrame(0, true)
                    }

                    override fun onError() {
                        parseListener?.onError()
                    }
                },
                true,
                null,
                animationPath
            )
        } catch (e: FileNotFoundException) {
            parseListener?.onError()
        }
    }
}