package com.mdzeldemt.versati

import android.app.Application
import android.content.Context
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application(), SingletonImageLoader.Factory {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // Initialize global crash reports
        with(ApplicationAcra) {
            init()
        }
    }

    override fun newImageLoader(context: PlatformContext) =
        // Initialize global image loader and cache
        ApplicationImageLoader.build(context)
}
