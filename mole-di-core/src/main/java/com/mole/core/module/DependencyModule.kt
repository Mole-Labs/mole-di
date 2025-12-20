package com.mole.core.module

import com.mole.core.dsl.DependencyModuleBuilder
import com.mole.core.scope.Scope

typealias ModuleDefinition = DependencyModuleBuilder.() -> Unit

data class DependencyModule(
    val factories: List<DependencyFactory<*>>,
)

fun combine(
    scope: Scope,
    block: ModuleDefinition,
) {
    val builder = DependencyModuleBuilder(scope)
    block(builder)
    scope.registerFactory(builder.build())
}

fun combine(
    scope: Scope,
    vararg block: ModuleDefinition,
) {
    block.forEach { combine(scope, it) }
}
