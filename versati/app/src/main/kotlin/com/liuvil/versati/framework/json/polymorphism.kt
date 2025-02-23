package com.liuvil.versati.framework.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

const val CLASS_DISCRIMINATOR = "_type"

inline fun <reified Base: Any> buildPolymorphicJson(
    crossinline moduleBuilder: PolymorphicModuleBuilder<Base>.() -> Unit
): Json = Json {
    serializersModule = SerializersModule {
        polymorphic(Base::class) {
            moduleBuilder()
        }
    }
    classDiscriminator = CLASS_DISCRIMINATOR
}