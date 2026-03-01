package com.liuvil.versati.framework.android

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openShareSheet(
    title: String,
    content: String
) {
    val intent = Intent(Intent.ACTION_SEND)
        .apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }

    startActivity(
        Intent.createChooser(intent, title)
    )
}

fun Context.openUrlExternally(
    uri: Uri
) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}