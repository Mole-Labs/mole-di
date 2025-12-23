package com.mole.core.qualifier

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
