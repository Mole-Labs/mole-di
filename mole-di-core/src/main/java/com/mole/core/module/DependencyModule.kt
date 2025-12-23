package com.mole.core.module

import com.mole.core.dsl.DependencyModuleBuilder
import com.mole.core.scope.DefaultScope

/**
 * A typealias for a lambda that defines a set of dependency bindings using the [DependencyModuleBuilder] DSL.
 * This allows for a clean and readable way to declare modules.
 *
 * Example:
 * ```
 * val myModule: ModuleDefinition = {
 *     single { MyService() }
 *     factory { MyPresenter(get()) }
 * }
 * ```
 */
typealias ModuleDefinition = DependencyModuleBuilder.() -> Unit

/**
 * A data class that holds a collection of [DependencyFactory] instances.
 * A module is essentially a container for the recipes (factories) that teach the DI container how to create objects.
 *
 * @property factories A list of factories that provide dependency instances.
 */
data class DependencyModule(
    val factories: List<DependencyFactory<*>>,
)

/**
 * Applies a [ModuleDefinition] to a given [DefaultScope], registering all the defined dependencies.
 *
 * @param scope The [DefaultScope] to which the module will be applied.
 * @param block The [ModuleDefinition] lambda containing the dependency bindings.
 */
fun combine(
    scope: DefaultScope,
    block: ModuleDefinition,
) {
    val builder = DependencyModuleBuilder(scope)
    block(builder)
    scope.registerFactory(builder.build())
}

/**
 * Applies multiple [ModuleDefinition]s to a given [DefaultScope].
 * This is a convenience function to register several modules at once.
 *
 * @param scope The [DefaultScope] to which the modules will be applied.
 * @param block A variable number of [ModuleDefinition] lambdas.
 */
fun combine(
    scope: DefaultScope,
    vararg block: ModuleDefinition,
) {
    block.forEach { combine(scope, it) }
}
