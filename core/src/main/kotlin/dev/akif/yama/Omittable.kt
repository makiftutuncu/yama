package dev.akif.yama

sealed interface Omittable<out T : Any?> {
    companion object {
        @JvmStatic
        fun <T> present(value: T): Omittable<T> =
            Present(value)

        @JvmStatic
        val omitted: Omittable<Nothing> =
            Omitted
    }

    fun getOrThrow(): T

    val isPresent: Boolean
        get() = this is Present

    val isOmitted: Boolean
        get() = this is Omitted

    fun whenPresent(action: (T) -> Unit) {
        if (this is Present) {
            action(value)
        }
    }

    fun whenOmitted(action: () -> Unit) {
        if (isOmitted) {
            action()
        }
    }
}

@JvmInline
value class Present<out T : Any?>(val value: T): Omittable<T> {
    override fun getOrThrow(): T = value
}

data object Omitted: Omittable<Nothing> {
    override fun getOrThrow(): Nothing = throw NoSuchElementException("Cannot get an omitted value")
}

fun <T> T.present(): Present<T> =
    Present(this)

fun <T> Omittable<T>.getOrElse(alternative: () -> T): T =
    when (this) {
        is Present -> value
        is Omitted -> alternative()
    }
