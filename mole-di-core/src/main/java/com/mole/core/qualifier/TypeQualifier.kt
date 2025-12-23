package com.mole.core.qualifier

import kotlin.reflect.KClass

/**
 * A [Qualifier] that uses a [KClass] as a unique identifier.
 * This is the most common type of qualifier, used to register and retrieve dependencies based on their class type.
 *
 * @property type The [KClass] used for identification.
 */
class TypeQualifier(
    val type: KClass<*>,
) : Qualifier {
    private val identifier: String? = type.qualifiedName

    private val cachedHashCode: Int = identifier.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeQualifier) return false
        return this.identifier == other.identifier
    }

    override fun hashCode(): Int = cachedHashCode

    override fun toString(): String = "TypeQualifier($identifier)"
}
