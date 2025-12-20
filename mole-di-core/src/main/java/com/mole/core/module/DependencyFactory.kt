package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier

sealed interface DependencyFactory<T : Any> {
    val qualifier: Qualifier
    val createRule: CreateRule
    val create: () -> T

    operator fun invoke() = create.invoke()
}
