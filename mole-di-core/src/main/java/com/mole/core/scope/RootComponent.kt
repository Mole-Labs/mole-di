package com.mole.core.scope

import com.mole.core.module.ModuleDefinition
import com.mole.core.module.combine

/**
 * Represents the root component of the dependency injection graph, typically the `Application` class.
 * It holds the root [Scope] from which all other scopes are derived.
 */
interface RootComponent {
    /**
     * The root scope of the application.
     */
    val scope: DefaultScope

    /**
     * Dynamically registers and combines additional dependency definitions into the root scope.
     * * This is primarily used during the application's startup phase to bootstrap the initial
     * dependency graph. By providing [ModuleDefinition]s, you can define how various
     * services and managers should be instantiated at the topmost level of the hierarchy.
     *
     * ### Usage Example:
     * ```
     * class MyTargetApplication : Application(), RootComponent {
     * override val scope = ScopeImpl(...)
     *
     * override fun onCreate() {
     * super.onCreate()
     * combineToRoot(networkModule, databaseModule)
     * }
     * }
     * ```
     *
     * @param block A variable number of [ModuleDefinition]s to be integrated into the root scope.
     * @see combine
     */
    fun combineToRoot(vararg block: ModuleDefinition) = combine(scope, *block)
}
