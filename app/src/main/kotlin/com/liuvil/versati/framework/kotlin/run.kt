package com.liuvil.versati.framework.kotlin

inline fun <T> T.runIf(
    condition: Boolean,
    block: T.() -> T
): T =
    if (condition)
        run(block)
    else
        this