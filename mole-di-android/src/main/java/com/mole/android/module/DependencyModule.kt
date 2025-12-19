package com.mole.android.module

import com.mole.android.dsl.module.DependencyModuleBuilder
import com.mole.android.scope.Scope

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
