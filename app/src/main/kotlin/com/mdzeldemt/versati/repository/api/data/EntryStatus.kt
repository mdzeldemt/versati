package com.mdzeldemt.versati.repository.api.data

import com.google.gson.annotations.SerializedName

enum class EntryStatus {
    @SerializedName("unread")
    UNREAD,

    @SerializedName("read")
    READ
}