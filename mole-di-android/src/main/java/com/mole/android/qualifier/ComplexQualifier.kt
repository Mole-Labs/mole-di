package com.mole.android.qualifier

class ComplexQualifier(
    val qualifier: Qualifier,
    val name: Any,
) : Qualifier {
    // 1. 생성 시점에 해시값을 미리 계산 (성능 핵심)
    // 31은 이펙티브 자바에서 권장하는 소수(Prime number)로, 해시 충돌을 줄여줍니다.
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
