package com.daedan.di.qualifier

import kotlin.reflect.KClass

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
