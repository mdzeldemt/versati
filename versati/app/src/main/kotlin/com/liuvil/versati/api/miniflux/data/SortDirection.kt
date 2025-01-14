package com.liuvil.versati.api.miniflux.data

import com.google.gson.annotations.SerializedName

enum class SortDirection {

    @SerializedName("asc")
    ASCENDING,

    @SerializedName("desc")
    DESCENDING

}