package com.mole.android.module

import com.mole.android.qualifier.CreateRule
import com.mole.android.qualifier.Qualifier

sealed interface DependencyFactory<T : Any> {
    val qualifier: Qualifier
    val createRule: CreateRule
    val create: () -> T

    operator fun invoke() = create.invoke()
}
