package com.liuvil.versati.repository.api.converter

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.OffsetDateTime

internal abstract class BodyConverterFactory: Converter.Factory() {
    companion object {
        fun create(): Converter.Factory =
            GsonConverterFactory.create(
                GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(
                        OffsetDateTime::class.java,
                        JsonDeserializer { json: JsonElement, _: Type, _: JsonDeserializationContext ->
                            OffsetDateTime.parse(
                                json.asString
                            )
                        } as JsonDeserializer<OffsetDateTime>
                    )
                    .create()
            )
    }
}
