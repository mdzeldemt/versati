package com.liuvil.versati

import org.acra.BuildConfig
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

object ApplicationAcra {

    fun Application.init() {
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
        }
    }
}