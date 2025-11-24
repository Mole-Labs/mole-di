package com.daedan.di.module

import com.daedan.di.qualifier.CreateRule
import com.daedan.di.qualifier.Qualifier

sealed interface DependencyFactory<T : Any> {
    val qualifier: Qualifier
    val createRule: CreateRule
    val create: () -> T

    operator fun invoke() = create.invoke()
}
