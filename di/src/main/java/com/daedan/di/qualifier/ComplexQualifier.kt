package com.daedan.di.qualifier

data class ComplexQualifier(
    val qualifier: Qualifier,
    val name: Any,
) : Qualifier
