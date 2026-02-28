package com.liuvil.versati.framework.compose

import androidx.compose.foundation.lazy.LazyListState

suspend fun LazyListState.scrollToStart() {
    scrollToItem(0)
}