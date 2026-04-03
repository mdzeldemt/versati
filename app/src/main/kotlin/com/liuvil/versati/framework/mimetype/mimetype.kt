package com.liuvil.versati.framework.mimetype

fun isImageMimeType(mimeType: String) =
    mimeType.startsWith("image/")
