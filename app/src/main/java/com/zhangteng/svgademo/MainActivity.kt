package com.zhangteng.svgademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zhangteng.svgalibrary.activity.AnimationPlayerActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AnimationPlayerActivity.start(this)
    }
}