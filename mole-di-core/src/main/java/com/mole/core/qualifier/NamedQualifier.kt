package com.mole.core.qualifier

/**
 * A [Qualifier] that uses a [String] name as a unique identifier.
 * This is useful for distinguishing between different instances of the same type.
 *
 * For example, to provide two different API base URLs:
 * ```
 * single(named("baseUrl")) { "https://api.example.com/" }
 * single(named("anotherBaseUrl")) { "https://api.another.com/" }
 * ```
 *
 * @property name The [String] used for identification.
 */
data class NamedQualifier(
    val name: String,
) : Qualifier
