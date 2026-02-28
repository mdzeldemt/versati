package com.liuvil.versati.framework.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun buildStyleElement(
    content: String
): Element =
    Element("style")
        .attr("type", "text/css")
        .html(content)

fun applyStylesheet(
    content: String,
    stylesheet: String
): String =
    Jsoup.parse(content)
        .appendChild(buildStyleElement(stylesheet))
        .html()
