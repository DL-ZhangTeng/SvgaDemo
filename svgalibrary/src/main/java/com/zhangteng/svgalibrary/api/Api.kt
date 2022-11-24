package com.zhangteng.svgalibrary.api

import com.zhangteng.svgalibrary.animation.AnimationEntity
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Url

interface Api {

    /**
     * 动效列表
     * @param url 预下载接口
     */
    @GET
    fun preDownload(@Url url: String?): Deferred<BaseResult<List<AnimationEntity>>>
}