package com.liuvil.versati.framework.validation

fun either(
    vararg results: Validation
): Validation =
    results.fold<Validation, Validation>(
        initial = Validation.Success,
    ) { current, next ->
        current or next
    }

fun validate(
    block: ValidationContext.() -> Unit
): Validation =
    ValidationContext()
        .apply(block)
        .result

class ValidationContext {

    var result: Validation = Validation.Success
        private set

    fun assert(
        condition: Boolean,
        failureMessage: String
    ) {
        result = result or
            if (condition) {
                Validation.Success
            } else {
                Validation.Failure(
                    message = failureMessage
                )
            }
    }

    fun fail(
        message: String
    ) {
        result = result or
            Validation.Failure(
                message = message
            )
    }
}

sealed class Validation {
    data object Success: Validation()

    data class Failure(
        val message: String
    ): Validation()

    fun <T> ifSuccess(
        block: () -> T
    ): T? {
        if (this is Success) {
            block()
        }
        return null
    }

    fun <T> ifFailure(
        block: (String) -> T
    ): T? {
        if (this is Failure) {
            block(message)
        }
        return null
    }

    infix fun or(
        other: Validation
    ): Validation =
        if (this is Failure) {
            this
        } else {
            other
        }
}