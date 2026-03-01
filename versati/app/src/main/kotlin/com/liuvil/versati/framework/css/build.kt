package com.liuvil.versati.framework.css

fun buildCssBlock(
    selector: String,
    attributes: Map<String, String>
): String =
    """
        $selector {
            ${attributes.map { (key, value) -> "$key : $value;" }.joinToString(separator = "\n")}
        }
    """.trimIndent()