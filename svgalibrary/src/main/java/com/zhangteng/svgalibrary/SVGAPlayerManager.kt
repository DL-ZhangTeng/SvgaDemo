package com.zhangteng.svgalibrary

import android.content.Context
import android.widget.RelativeLayout
import com.blankj.utilcode.util.ScreenUtils
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAVideoEntity
import com.zhangteng.httputils.http.HttpUtils
import com.zhangteng.httputils.result.coroutine.callback.DeferredDownloadCallBack
import com.zhangteng.httputils.result.coroutine.deferredGo
import com.zhangteng.httputils.result.coroutine.deferredGoIResponse
import com.zhangteng.svgalibrary.animation.AnimationEntity
import com.zhangteng.svgalibrary.animation.AnimationPlayer
import com.zhangteng.svgalibrary.animation.ParseListener
import com.zhangteng.svgalibrary.api.Api
import com.zhangteng.svgalibrary.audio.AudioPlayer
import com.zhangteng.svgalibrary.check.CompletenessCheck
import com.zhangteng.svgalibrary.check.CompletenessCheckListener
import com.zhangteng.svgalibrary.config.ConfigEntity
import com.zhangteng.svgalibrary.download.DownloadCallBack
import com.zhangteng.utils.*
import com.zhangteng.utils.JsonUtils.readJson
import com.zhangteng.utils.UnzipUtils.unzipFile
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException

/**
 * description: Svga动画管理
 * author: Swing
 * date: 2022/11/8
 */
object SVGAPlayerManager {
    private var animationDir: String? = null

    fun init(context: Context) {
        animationDir = context.externalCacheDir!!.absolutePath + File.separator + "svga"
        AnimationPlayer.init(context)
        AudioPlayer.init()
    }

    /**
     * description 延时动效预下载
     *
     * @param url 礼物列表接口
     * @param delay 秒
     */
    fun preDownload(url: String, delay: Int) {
        ThreadPoolUtils.instance.addDelayExecuteTask({ preDownload(url) }, delay * 1000L)
    }

    /**
     * description 动效预下载
     *
     * @param url 礼物列表接口
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun preDownload(url: String) {
        //获取动效列表
        GlobalScope.launch {
            HttpUtils.instance.ConfigGlobalHttpUtils()
                .createService(Api::class.java)
                .preDownload(url)
                .deferredGoIResponse(
                    {
                        preDownload(it.getResult())
                    },
                    {

                    },
                    {

                    })
        }
    }

    /**
     * description 动效预下载
     *
     * @param animations 礼物列表
     */
    fun preDownload(animations: List<AnimationEntity>) {
        ThreadPoolUtils.instance.addExecuteTask {
            for (entity in animations) {
                //动效解压路径
                val unZipPath = getAnimationUnzipDir(entity)
                //动效压缩包名称
                val zipName = getAnimationZipName(entity)
                //解压文件夹
                val unzipDir = File(unZipPath)
                //压缩包文件
                val zipFile = File(animationDir, zipName)
                //压缩包存在 校验并解压  压缩包不存在 下载校验并解压
                CompletenessCheck.check(
                    zipFile,
                    entity.checkCode,
                    object : CompletenessCheckListener {
                        override fun onCompleteness(isCompleteness: Boolean) {
                            if (!isCompleteness) {
                                try {
                                    if (!zipFile.delete()) {
                                        return
                                    }
                                    if (!zipFile.createNewFile()) {
                                        return
                                    }
                                    download(entity)
                                } catch (ignore: IOException) {
                                }
                            } else {
                                //解压文件夹不存在
                                if (!unzipDir.exists()) {
                                    //创建解压文件夹，如果失败直接退出本动效下载
                                    if (unzipDir.mkdirs()) {
                                        unzipFile(
                                            zipFile.absolutePath,
                                            getAnimationUnzipDir(entity),
                                            null
                                        )
                                    }
                                }
                            }
                        }
                    })
            }
        }
    }

    /**
     * description 下载文件到animationDir，并播放动效
     *
     * @param animationView 动画view
     * @param entity        资源
     * @param parseListener 解析回调
     */
    fun download(
        animationView: SVGAImageView?,
        entity: AnimationEntity,
        parseListener: ParseListener?
    ) {
        //如果文件不存在，下载文件，并展示动效
        download(entity, object : DownloadCallBack {
            override fun onFailure(iException: IException?) {

            }

            override fun onSuccess(filePath: String?) {
                loadAnimation(animationView, entity, true, parseListener)
            }
        })
    }

    /**
     * description 下载文件到animationDir,并解压
     *
     * @param entity 资源
     * @param downloadCallBack 下载回调
     */
    @OptIn(DelicateCoroutinesApi::class)
    @JvmOverloads
    fun download(entity: AnimationEntity, downloadCallBack: DownloadCallBack? = null) {
        //动效压缩包名称
        val zipName = getAnimationZipName(entity)
        //压缩包文件
        val zip = File(animationDir, zipName)
        //动效解压包
        val unZipPath = getAnimationUnzipDir(entity)

        //删除解压目录
        unZipPath.deleteDir(true)
        //删除动效压缩包
        zip.absolutePath.deleteFile()

        //重新下载
        GlobalScope.launch {
            HttpUtils.instance.DownloadRetrofit()
                .downloadFileByDeferred(entity.animationUrl)
                .deferredGo(object : DeferredDownloadCallBack(entity.checkCode, null) {

                    override fun onSuccess(
                        bytesRead: Long,
                        contentLength: Long,
                        progress: Float,
                        done: Boolean,
                        filePath: String?
                    ) {
                        if (done) {
                            if (filePath?.isNotEmpty() == true) {
                                val zipFile = File(filePath)
                                CompletenessCheck.check(zipFile, entity.checkCode,
                                    object : CompletenessCheckListener {
                                        override fun onCompleteness(isCompleteness: Boolean) {
                                            if (!isCompleteness) {
                                                GlobalScope.launch {
                                                    try {
                                                        zipFile.delete()
                                                        withContext(Dispatchers.IO) {
                                                            zipFile.createNewFile()
                                                        }
                                                        download(entity, downloadCallBack)
                                                    } catch (e: IOException) {
                                                        downloadCallBack?.onFailure(
                                                            IException(
                                                                e.localizedMessage,
                                                                IException.ERROR.UNKNOWN
                                                            )
                                                        )
                                                    }
                                                }
                                            } else {
                                                unzipFile(
                                                    zipFile.absolutePath,
                                                    getAnimationUnzipDir(entity),
                                                    object : UnzipUtils.UnzipListener {
                                                        override fun onUnzip(
                                                            unzip: File?,
                                                            isUnzip: Boolean
                                                        ) {
                                                            if (isUnzip) {
                                                                downloadCallBack?.onSuccess(filePath)
                                                            } else {
                                                                downloadCallBack?.onFailure(
                                                                    IException(
                                                                        "解压失败",
                                                                        IException.ERROR.UNKNOWN
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    })
                                            }
                                        }
                                    })
                            } else {
                                downloadCallBack?.onFailure(
                                    IException(
                                        "下载失败",
                                        IException.ERROR.UNKNOWN
                                    )
                                )
                            }
                        }
                    }

                    override fun onFailure(iException: IException?) {
                        downloadCallBack?.onFailure(iException)
                    }
                })
        }
    }

    /**
     * description 直接加载网络动画，可调整显示位置，均全屏显示
     *
     * @param animationView 动画view
     * @param url           资源
     * @param centerX       中心点坐标
     * @param centerY       中心点坐标
     * @param isLayout      是否根据配置文件重设View大小位置，可能造成动效中的音频无法关闭的bug，原因未知
     * @param parseListener 解析回调
     */
    @JvmOverloads
    fun loadAnimation(
        animationView: SVGAImageView?,
        url: String?,
        centerX: Float = 0.5f,
        centerY: Float = 0.5f,
        isLayout: Boolean = true,
        parseListener: ParseListener? = null
    ) {
        AnimationPlayer
            .loadAnimationFromURL(
                animationView,
                url,
                object : ParseListener {
                    override fun onComplete(videoEntity: SVGAVideoEntity?) {
                        if (isLayout) {
                            val configEntity = ConfigEntity()
                            configEntity.animationWidth = 1.0f
                            configEntity.animationHeight = 1.0f
                            configEntity.centerX = centerX
                            configEntity.centerY = centerY
                            layoutAnimationView(animationView, configEntity, videoEntity, 0)
                        }
                        parseListener?.onComplete(videoEntity)
                    }

                    override fun onError() {
                        parseListener?.onError()
                    }
                }
            )
    }

    /**
     * description 加载zip网络动画并使用配置文件
     * 压缩包结构->xxx.svga
     * ->xxx.json
     * ->{"checkCode":"7382cb1f9555617d3a67e3a77fef4786","animationHeight":0.5,"animationWidth":0.5,"centerX":0.5,"centerY":0.5}
     *
     * @param animationView 动画view
     * @param entity        资源
     * @param isLayout      是否根据配置文件重设View大小位置，可能造成动效中的音频无法关闭的bug，原因未知
     * @param parseListener 解析回调
     */
    @JvmOverloads
    fun loadAnimation(
        animationView: SVGAImageView?,
        entity: AnimationEntity,
        isLayout: Boolean = true,
        parseListener: ParseListener? = null
    ) {
        //动效解压包
        val unZipPath = getAnimationUnzipDir(entity)
        //动效名称
        val animationName = getAnimationName(entity)
        //动效配置文件名称
        val configName = getAnimationConfigName(entity)
        //动效文件
        val animation = File(unZipPath, animationName)
        //动效配置文件
        val config = File(unZipPath, configName)
        val configEntity = readJson(config, ConfigEntity::class.java)
        //动效文件存在，展示动画
        if (animation.exists()) {
            //配置文件存在，展示动画
            if (configEntity != null) {
                //校验解压后的动画文件是否完整，完整-》播放；不完整-》重新下载
                CompletenessCheck.Companion.check(
                    animation,
                    configEntity.checkCode,
                    object : CompletenessCheckListener {
                        override fun onCompleteness(isCompleteness: Boolean) {
                            if (!isCompleteness) {
                                //如果文件不存在，下载文件，并展示动效
                                download(animationView, entity, parseListener)
                            } else {
                                AnimationPlayer
                                    .loadAnimationFromPath(
                                        animationView,
                                        animation.absolutePath,
                                        object : ParseListener {
                                            override fun onComplete(videoEntity: SVGAVideoEntity?) {
                                                if (isLayout) {
                                                    layoutAnimationView(
                                                        animationView,
                                                        configEntity,
                                                        videoEntity,
                                                        0
                                                    )
                                                }
                                                parseListener?.onComplete(videoEntity)
                                            }

                                            override fun onError() {
                                                parseListener?.onError()
                                            }
                                        }
                                    )
                            }
                        }
                    })
            } else {
                //如果文件不存在，下载文件，并展示动效
                download(animationView, entity, parseListener)
            }
        } else {
            //如果文件不存在，下载文件，并展示动效
            download(animationView, entity, parseListener)
        }
    }

    /**
     * description 暂停动画
     *
     * @param svgaImageView 动效播放View
     */
    fun pauseAnimation(svgaImageView: SVGAImageView?) {
        svgaImageView?.pauseAnimation()
    }

    /**
     * description 停止动画
     *
     * @param svgaImageView 动效播放View
     */
    fun stopAnimation(svgaImageView: SVGAImageView?) {
        svgaImageView?.stopAnimation()
    }

    /**
     * description 清理动画
     *
     * @param svgaImageView 动效播放View
     */
    fun clearAnimation(svgaImageView: SVGAImageView?) {
        svgaImageView?.clear()
        svgaImageView?.clearAnimation()
    }

    /**
     * description 释放音频资源
     */
    fun releaseSound() {
        AudioPlayer.release()
    }

    /**
     * description 使用配置文件重布局view
     *
     * @param animationView 动画view
     * @param configEntity  配置
     * @param videoEntity   动效尺寸信息
     * @param scaleType     缩放方式：-1 不进行缩放 0 压缩动效使能够全部展示（会出现边界） 1 拉伸动效使能够全部展示（会超出屏幕）
     */
    fun layoutAnimationView(
        animationView: SVGAImageView?,
        configEntity: ConfigEntity,
        videoEntity: SVGAVideoEntity?,
        scaleType: Int
    ) {
        animationView?.post {
            var layoutParams =
                animationView.layoutParams as RelativeLayout.LayoutParams?
            if (layoutParams == null) {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            }
            val screenWidth = ScreenUtils.getScreenWidth()
            val screenHeight = ScreenUtils.getScreenHeight()
            var viewWidth = (screenWidth * configEntity.animationWidth).toInt()
            var viewHeight = (screenHeight * configEntity.animationHeight).toInt()
            if (videoEntity != null) {
                val width = videoEntity.videoSize.width.toInt()
                val height = videoEntity.videoSize.height.toInt()

                //是否宽度较大
                val b = width * 1.0f / height > viewWidth * 1.0f / viewHeight
                if (scaleType == 0) {
                    //如果动效高度较大，则缩放View高度使View高度保持全屏
                    //如果动效宽度较大，则缩放View宽度使View宽度保持全屏
                    if (b) {
                        viewHeight = height * viewWidth / width
                    } else {
                        viewWidth = width * viewHeight / height
                    }
                } else if (scaleType == 1) {
                    //全屏适配，如果动效高度较大，则拉大View高度使View宽度保持全屏
                    //全屏适配，如果动效宽度较大，则拉大View宽度使View高度保持全屏
                    if (b) {
                        viewWidth = width * viewHeight / height
                    } else {
                        viewHeight = height * viewWidth / width
                    }
                }
                layoutParams.width = viewWidth
                layoutParams.height = viewHeight
            }
            layoutParams.leftMargin =
                (screenWidth * configEntity.centerX - viewWidth * configEntity.animationWidth / 2).toInt()
            layoutParams.topMargin =
                (screenHeight * configEntity.centerY - viewHeight * configEntity.animationHeight / 2).toInt()
            animationView.parent?.requestLayout()
        }
    }

    /**
     * description 通过动效类获取动效压缩包.zip存储名称（存储为${md5}.zip）
     *
     * @param entity 动效类
     */
    private fun getAnimationZipName(entity: AnimationEntity): String {
        return entity.checkCode + entity.animationUrl!!.substring(
            entity.animationUrl!!.lastIndexOf(
                "."
            )
        )
    }

    /**
     * description 通过动效类获取动效.svga存储名称
     *
     * @param entity 动效类
     */
    private fun getAnimationName(entity: AnimationEntity): String {
        //通过url获取远端压缩包名称
        val zipName = entity.animationUrl!!.substring(entity.animationUrl!!.lastIndexOf("/") + 1)
        var index = zipName.lastIndexOf(".")
        if (index < 0) {
            index = zipName.length
        }
        return zipName.substring(0, index) + ".svga"
    }

    /**
     * description 通过动效类获取配置.json存储名称
     *
     * @param entity 动效类
     */
    private fun getAnimationConfigName(entity: AnimationEntity): String {
        //通过url获取远端压缩包名称
        val zipName = entity.animationUrl!!.substring(entity.animationUrl!!.lastIndexOf("/") + 1)
        var index = zipName.lastIndexOf(".")
        if (index < 0) {
            index = zipName.length
        }
        return zipName.substring(0, index) + ".json"
    }

    /**
     * description 通过动效类获取动效解压文件夹名称（存储为${md5}）
     *
     * @param entity 动效类
     */
    private fun getAnimationUnzipDir(entity: AnimationEntity): String {
        //以文件名为文件夹解压文件
        return animationDir + File.separator + entity.checkCode
    }
}