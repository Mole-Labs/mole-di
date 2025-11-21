package com.daedan.di

import com.daedan.di.dsl.DependencyModuleBuilder
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

data class DependencyModule(
    val factories: List<DependencyFactory<*>>,
)

fun module(
    scope: Scope,
    block: DependencyModuleBuilder.() -> Unit,
): DependencyModule {
    val builder = DependencyModuleBuilder(scope)
    block(builder)
    return builder.build()
}

inline fun <reified T : Any> module(
    qualifier: Qualifier = TypeQualifier(T::class),
    scope: Scope = Scope(qualifier),
    noinline block: DependencyModuleBuilder.() -> Unit,
): DependencyModule = module(scope, block)
