package com.mole.android.qualifier

interface Qualifier {
    override fun toString(): String

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}
