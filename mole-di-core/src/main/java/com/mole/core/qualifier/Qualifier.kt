package com.mole.core.qualifier

/**
 * A marker interface used to uniquely identify dependencies within the dependency injection container.
 *
 * Qualifiers are essential for distinguishing between different instances of the same type.
 * For example, if you need two different String instances (e.g., an API key and a database name),
 * you can use different qualifiers to register and retrieve them.
 *
 * Implementations of this interface **must** correctly override `equals()` and `hashCode()`
 * to ensure that the DI container can accurately identify and cache dependencies.
 */
interface Qualifier {
    override fun toString(): String

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}
