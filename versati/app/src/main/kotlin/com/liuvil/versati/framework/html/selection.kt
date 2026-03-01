package com.liuvil.versati.framework.html

import org.jsoup.nodes.Document
import java.net.URL

fun extractImageUrls(
    document: Document
): List<URL> {
    // TODO: Make configurable
    return document.getElementsByTag("img")
        .mapNotNull { it.attribute("src") }
        .map { URL(it.value) }
}