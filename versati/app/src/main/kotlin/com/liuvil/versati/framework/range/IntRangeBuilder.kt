package com.liuvil.versati.framework.range

class IntRangeBuilder {
    private var start: Int? = null
    private var endInclusive: Int? = null

    fun isEmpty(): Boolean = start == null || endInclusive == null

    fun extendTo(value: Int) {
        if (start == null) {
            start = value
        }
        endInclusive = value
    }

    fun clear() {
        start = null
        endInclusive = null
    }

    fun toIntRange(): IntRange =
        start?.let { start ->
            endInclusive?.let { end ->
                IntRange(start = start, endInclusive = end + 1)
            }
        } ?: throw IllegalStateException()

}