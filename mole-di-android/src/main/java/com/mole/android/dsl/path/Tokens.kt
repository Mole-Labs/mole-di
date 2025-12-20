package com.mole.android.dsl.path

import com.mole.core.qualifier.ComplexQualifier

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
