package com.mdzeldemt.versati.framework.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun decodeBitmap(
    data: String
): Bitmap {
    val decodedBytes = Base64.decode(
        data.substringAfter(","),
        Base64.DEFAULT
    )
    return BitmapFactory.decodeByteArray(
        decodedBytes,
        0,
        decodedBytes.size
    )
}
