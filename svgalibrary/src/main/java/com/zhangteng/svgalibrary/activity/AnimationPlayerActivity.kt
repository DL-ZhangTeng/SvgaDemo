package com.zhangteng.svgalibrary.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.gyf.immersionbar.ImmersionBar
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGAImageView
import com.zhangteng.base.base.BaseActivity
import com.zhangteng.svgalibrary.R
import com.zhangteng.svgalibrary.SVGAPlayerManager
import com.zhangteng.svgalibrary.animation.AnimationEntity

/**
 * description:动效播放
 * author: Swing
 * date: 2022/11/15
 */
class AnimationPlayerActivity : BaseActivity() {
    private var svgaImageView: SVGAImageView? = null
    private var animationEntity: AnimationEntity? = null

    /**
     * description: 动效文件路径
     */
    private var animationUrl: String? = null

    /**
     * description: 动效中心X轴位置
     */
    private var centerX = 0f

    /**
     * description: 动效中心Y轴位置
     */
    private var centerY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.svgalibrary_activity_animation_player)
    }

    public override fun initView() {
        if (intent != null) {
            if (intent.hasExtra("animationEntity")) {
                animationEntity = intent.getSerializableExtra("animationEntity") as AnimationEntity?
            } else if (intent.hasExtra("checkCode") && intent.hasExtra("animationUrl")) {
                animationEntity = AnimationEntity()
                animationEntity!!.checkCode = intent.getStringExtra("checkCode")
                animationEntity!!.animationUrl = intent.getStringExtra("animationUrl")
            } else if (intent.hasExtra("animationUrl")
                &&
                intent.hasExtra("centerX") && intent.hasExtra("centerY")
            ) {
                animationUrl = intent.getStringExtra("animationUrl")
                centerX = intent.getFloatExtra("centerX", 0.5f)
                centerY = intent.getFloatExtra("centerY", 0.5f)
            }
        }
        svgaImageView = findViewById(R.id.svgaImageView)
        svgaImageView?.callback = object : SVGACallback {
            override fun onPause() {}
            override fun onFinished() {
                finish()
            }

            override fun onRepeat() {}
            override fun onStep(frame: Int, percentage: Double) {}
        }
    }

    public override fun initData() {
        if (animationEntity == null && TextUtils.isEmpty(animationUrl)) {
            //测试动画
            animationEntity = AnimationEntity()
            animationEntity!!.checkCode = "8a1d8717c23608e690ebbf8c00e8efc2"
            animationEntity!!.animationUrl = "http://tp.kaishuihu.com/test/mp3_to_long.zip"
        }
        if (animationEntity != null && !TextUtils.isEmpty(animationEntity!!.animationUrl)) {
            SVGAPlayerManager.loadAnimation(svgaImageView, animationEntity!!)
        } else if (!TextUtils.isEmpty(animationUrl)) {
            SVGAPlayerManager.loadAnimation(svgaImageView, animationUrl, centerX, centerY)
        } else {
            finish()
        }
    }

    public override fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this)
            .transparentStatusBar()
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
    }

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
    }

    override fun onPause() {
        super.onPause()
        SVGAPlayerManager.pauseAnimation(svgaImageView)
    }

    override fun onStop() {
        super.onStop()
        SVGAPlayerManager.stopAnimation(svgaImageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        SVGAPlayerManager.clearAnimation(svgaImageView)
        SVGAPlayerManager.releaseSound()
    }

    companion object {
        /**
         * description 展示测试动效
         */
        fun start(context: Context) {
            val intent = Intent(context, AnimationPlayerActivity::class.java)
            context.startActivity(intent)
        }

        /**
         * description 使用压缩包展示动效
         *
         * @param animationEntity 动效类
         */
        fun start(context: Context, animationEntity: AnimationEntity?) {
            val intent = Intent(context, AnimationPlayerActivity::class.java)
            intent.putExtra("animationEntity", animationEntity)
            context.startActivity(intent)
        }

        /**
         * description 使用压缩包展示动效
         *
         * @param checkCode    压缩包文字校验码
         * @param animationUrl 动效url
         */
        fun start(context: Context, checkCode: String?, animationUrl: String?) {
            val intent = Intent(context, AnimationPlayerActivity::class.java)
            intent.putExtra("checkCode", checkCode)
            intent.putExtra("animationUrl", animationUrl)
            context.startActivity(intent)
        }

        /**
         * description 使用url展示动效
         *
         * @param animationUrl 动效url
         * @param centerX      动效坐标
         * @param centerY      动效坐标
         */
        fun start(context: Context, animationUrl: String?, centerX: String?, centerY: String?) {
            val intent = Intent(context, AnimationPlayerActivity::class.java)
            intent.putExtra("animationUrl", animationUrl)
            intent.putExtra("centerX", centerX)
            intent.putExtra("centerY", centerY)
            context.startActivity(intent)
        }
    }
}