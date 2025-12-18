package com.daedan.di.dsl.path

import com.daedan.di.qualifier.ComplexQualifier

@JvmInline
value class ViewModelScopeToken(
    val value: ComplexQualifier,
)

@JvmInline
value class ActivityScopeToken(
    val value: ComplexQualifier,
)

@JvmInline
value class ActivityRetainedScopeToken(
    val value: ComplexQualifier,
)
