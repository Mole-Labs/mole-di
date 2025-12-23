package com.mole.core.module

import com.mole.core.dsl.DependencyModuleBuilder
import com.mole.core.scope.ScopeImpl

typealias ModuleDefinition = DependencyModuleBuilder.() -> Unit

data class DependencyModule(
    val factories: List<DependencyFactory<*>>,
)

fun combine(
    scope: ScopeImpl,
    block: ModuleDefinition,
) {
    val builder = DependencyModuleBuilder(scope)
    block(builder)
    scope.registerFactory(builder.build())
}

fun combine(
    scope: ScopeImpl,
    vararg block: ModuleDefinition,
) {
    block.forEach { combine(scope, it) }
}
