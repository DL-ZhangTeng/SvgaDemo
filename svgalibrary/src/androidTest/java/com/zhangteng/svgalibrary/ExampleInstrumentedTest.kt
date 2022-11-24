package com.zhangteng.svgalibrary

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zhangteng.svgalibrary.animation.AnimationEntity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.zhangteng.svgalibrary", appContext.packageName)
    }

    @Test
    fun preDownload() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        SVGAPlayerManager.init(appContext)
        val animationEntities: MutableList<AnimationEntity> = ArrayList()
        val animationEntity = AnimationEntity()
        animationEntity.checkCode = "1be18c0c43dfa27d6b1c3910e2884443"
        animationEntity.animationUrl = "http://tp.kaishuihu.com/test/mp3_to_long.zip"
        animationEntities.add(animationEntity)
        SVGAPlayerManager.preDownload(animationEntities)
        Assert.assertEquals("com.zhangteng.svgalibrary", appContext.packageName)
    }
}