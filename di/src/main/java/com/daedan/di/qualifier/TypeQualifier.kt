package com.daedan.di.qualifier

import kotlin.reflect.KClass

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
