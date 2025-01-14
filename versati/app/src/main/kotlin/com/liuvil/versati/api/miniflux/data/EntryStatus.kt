package com.liuvil.versati.api.miniflux.data

import com.google.gson.annotations.SerializedName

enum class EntryStatus {
    @SerializedName("unread")
    UNREAD,

    @SerializedName("read")
    READ
}