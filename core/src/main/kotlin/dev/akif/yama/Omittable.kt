package dev.akif.yama

/**
 * Wrapper type for properties that can be omitted from a payload
 *
 * This serves the purpose of distinguishing whether the property is omitted in the payload or it is explicitly set to `null`.
 *
 * @param T Type of the property, which can be nullable
 *
 * @see [Present]
 * @see [Omitted]
 */
sealed interface Omittable<out T : Any?> {
    companion object {
        /**
         * Builds an omittable value to be present
         *
         * @param value The value
         *
         * @return Given value as [Present]
         */
        @JvmStatic
        fun <T> present(value: T): Omittable<T> = Present(value)

        /**
         * Builds an omitted value
         *
         * @return The [Omitted] value
         */
        @JvmStatic
        val omitted: Omittable<Nothing> = Omitted
    }

    /**
     * Gets the value in this [Omittable]
     *
     * @return The value if this is [Present]
     *
     * @throws [NoSuchElementException] if this is [Omitted]
     */
    fun getOrThrow(): T

    /**
     * Gets whether this [Omittable] is [Present]
     */
    val isPresent: Boolean
        get() = this is Present

    /**
     * Gets whether this [Omittable] is [Omitted]
     */
    val isOmitted: Boolean
        get() = this is Omitted

    /**
     * Runs given action when this [Omittable] is [Present]
     *
     * @param action An action to run with the value as input
     */
    fun whenPresent(action: (T) -> Unit) {
        if (this is Present) {
            action(value)
        }
    }

    /**
     * Runs given action when this [Omittable] is [Omitted]
     *
     * @param action An action to run without any input
     */
    fun whenOmitted(action: () -> Unit) {
        if (isOmitted) {
            action()
        }
    }
}

/**
 * Present variant of an [Omittable] indicating the value exists
 *
 * @param value The value that exists
 *
 * @param T Type of the value
 */
@JvmInline
value class Present<out T : Any?>(
    val value: T
) : Omittable<T> {
    override fun getOrThrow(): T = value
}

/**
 * Omitted variant of an [Omittable] indicating value is omitted
 */
data object Omitted : Omittable<Nothing> {
    override fun getOrThrow(): Nothing = throw NoSuchElementException("Cannot get an omitted value")
}

/**
 * Builds an omittable value to be present
 *
 * @receiver The value
 *
 * @return Given value as [Present]
 */
fun <T> T.present(): Present<T> = Present(this)

/**
 * Gets the value in this [Omittable] or provided the alternative
 *
 * @param alternative A supplier of an alternative value
 *
 * @return The value if this is [Present] or the alternative if this is [Omitted]
 */
fun <T> Omittable<T>.getOrElse(alternative: () -> T): T =
    when (this) {
        is Present -> value
        is Omitted -> alternative()
    }
