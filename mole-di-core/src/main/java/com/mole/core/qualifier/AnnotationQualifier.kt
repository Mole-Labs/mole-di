package com.mole.core.qualifier

import kotlin.reflect.KClass

/**
 * A [Qualifier] that uses an [Annotation] class as a unique identifier.
 * This allows for creating custom, type-safe qualifiers for dependency injection.
 *
 * Example usage:
 * ```
 * // 1. Define a custom annotation
 * annotation class ApiService
 *
 * // 2. Register a dependency with the annotation qualifier
 * single(annotated<ApiService>()) { Retrofit.create(MyApi::class.java) }
 *
 * // 3. Inject the dependency
 * val myApi: MyApi by scope.inject(annotated<ApiService>())
 * ```
 *
 * @property annotation The [KClass] of the annotation used for identification.
 */
class AnnotationQualifier(
    val annotation: KClass<out Annotation>,
) : Qualifier {
    private val identifier: String = annotation.qualifiedName ?: annotation.toString()
    private val cachedHashCode: Int = identifier.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationQualifier) return false
        return this.identifier == other.identifier
    }

    override fun hashCode(): Int = cachedHashCode

    override fun toString(): String = "@$identifier"
}
