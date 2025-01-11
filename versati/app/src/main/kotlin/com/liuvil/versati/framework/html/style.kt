package com.liuvil.versati.framework.html

import org.jsoup.nodes.Element

fun buildStyleElement(
    content: String
): Element =
    Element("style")
        .attr("type", "text/css")
        .html(content)
