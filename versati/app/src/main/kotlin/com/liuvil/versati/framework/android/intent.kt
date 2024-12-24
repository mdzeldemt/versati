package com.liuvil.versati.framework.android

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openURLExternally(
    uri: Uri,
    context: Context
) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}