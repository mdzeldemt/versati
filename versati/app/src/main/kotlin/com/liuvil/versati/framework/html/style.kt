package com.liuvil.versati.framework.html

import org.jsoup.Jsoup

fun applyStyling(
    content: String,
    style: String
): String {
    val document = Jsoup.parse(content)

    val styleElement = document.createElement("style")
    styleElement.attr("type", "text/css")
    styleElement.html(style)
    document.appendChild(styleElement)

    return document.html()
}