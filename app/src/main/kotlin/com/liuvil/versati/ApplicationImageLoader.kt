package com.liuvil.versati

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory

object ApplicationImageLoader {
    private const val DISK_CACHE_MAX_SIZE = 250L * 1024 * 1024 // 250 MB
    private const val DISK_CACHE_DIR = "image_cache"

    fun build(
        context: Context
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve(DISK_CACHE_DIR))
                    .maxSizeBytes(DISK_CACHE_MAX_SIZE)
                    .build()
            }
            .build()
    }
}