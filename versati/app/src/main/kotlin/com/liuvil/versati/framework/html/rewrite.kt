package com.liuvil.versati.framework.html

import org.jsoup.nodes.Document

sealed interface RewriteRule {
    fun apply(document: Document)
}

data class ElementWhitelistRule(
    val selectors: List<String>
): RewriteRule {

    override fun apply(document: Document) {
        val tagSelector = selectors.joinToString(separator = "") { ":not(${it})" }
        document.select(tagSelector).remove()
    }

}

data class AttributeWhitelistRule(
    val selector: String,
    val attributeNames: Set<String>
): RewriteRule {

    override fun apply(document: Document) {
        document.select(selector)
            .forEach { element ->
                element.attributes()
                    .map { it.key }
                    .filter { !attributeNames.contains(it) }
                    .forEach { element.removeAttr(it) }
            }
    }

}

data class AttributeRewriteRule(
    val selector: String,
    val attributeName: String,
    val attributeValue: String
): RewriteRule {

    override fun apply(document: Document) {
        document.select(selector)
            .forEach { element ->
                element.attr(attributeName, attributeValue)
            }
    }

}
