package com.mdzeldemt.versati.framework.compose

import androidx.compose.foundation.lazy.LazyListState

suspend fun LazyListState.scrollToStart() {
    scrollToItem(0)
}