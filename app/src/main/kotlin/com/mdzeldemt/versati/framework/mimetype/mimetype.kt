package com.mdzeldemt.versati.framework.mimetype

fun isImageMimeType(mimeType: String) =
    mimeType.startsWith("image/")
