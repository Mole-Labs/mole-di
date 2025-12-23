package com.mole.core.qualifier

/**
 * A [Qualifier] that combines another [Qualifier] and a name object to create a more specific, composite identifier.
 * This is particularly useful for creating nested scopes or for qualifying dependencies within a specific context,
 * It **goes great with the Android Lifecycle**. like associating a scope with a specific Activity AND a scope key (e.g., `ActivityScope`).
 *
 * The `hashCode` is pre-calculated at creation time for performance optimization.
 *
 * @property qualifier The base [Qualifier] (e.g., a [TypeQualifier] for an Activity class).
 * @property name An additional object to make the qualifier unique
 */
class ComplexQualifier(
    val qualifier: Qualifier,
    val name: Any,
) : Qualifier {
    // 1. Calculate hash code at creation time (Performance key)
    // 31 is a prime number recommended in Effective Java to reduce hash collisions.
    private val cachedHashCode: Int =
        run {
            var result = qualifier.hashCode()
            result = 31 * result + name.hashCode()
            result
        }

    override fun hashCode(): Int = cachedHashCode

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexQualifier) return false
        return this.qualifier == other.qualifier && this.name == other.name
    }

    override fun toString(): String = "ComplexQualifier(q=$qualifier, name=$name)"
}
