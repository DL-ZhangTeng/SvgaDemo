package com.zhangteng.svgalibrary

import com.zhangteng.httputils.gsonadapter.FailOverGson
import com.zhangteng.svgalibrary.animation.AnimationEntity
import com.zhangteng.utils.MD5Util
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun toJson() {
        val entity = AnimationEntity()
        entity.animationUrl = "xxx"
        entity.checkCode = "xxx"
        val json: String = FailOverGson.failOverGson.toJson(entity)
        println("-----------$json")
        Assert.assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun toMd5() {
        val md5: String = MD5Util.getFileMD5("F:\\开发文档\\mp3_to_long\\mp3_to_long.svga")
        println("-----------$md5")
        Assert.assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun zipToMd5() {
        val md5: String = MD5Util.getFileMD5("F:\\开发文档\\mp3_to_long.zip")
        println("-----------$md5")
        Assert.assertEquals(4, (2 + 2).toLong())
    }
}