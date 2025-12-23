package com.mole.core.scope

import com.mole.core.module.DependencyModule
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier

/**
 * Represents a lexical scope that holds and manages dependency instances.
 * Scopes can be nested to create a hierarchy, allowing for managing dependencies with different lifecycles.
 * Instances resolved in a child scope can access dependencies from its parent scope.
 */
interface Scope {
    /**
     * Resolves and returns a dependency instance that matches the given [qualifier].
     * If the instance is a singleton and already created, it returns the cached instance.
     * If it's a factory, a new instance is created on each call.
     * If the instance is not found in the current scope, it will search in the parent scope.
     *
     * @param qualifier The unique identifier for the dependency to resolve.
     * @return The resolved dependency instance.
     * @throws IllegalStateException if the dependency cannot be found or if a circular dependency is detected.
     */
    fun get(qualifier: Qualifier): Any

    /**
     * Retrieves a direct child scope identified by the given [qualifier].
     *
     * @param qualifier The unique identifier for the child scope.
     * @return The child [Scope] instance.
     * @throws IllegalStateException if the sub-scope is not defined.
     */
    fun getSubScope(qualifier: Qualifier): Scope

    /**
     * Navigates through the scope hierarchy and returns a descendant scope specified by the [path].
     * The path is resolved from the current scope downwards.
     *
     * @param path The [Path] object describing the route to the target scope.
     * @return The target descendant [Scope].
     * @throws IllegalStateException if any scope in the path is not found.
     */
    fun resolvePath(path: Path): Scope

    /**
     * Manually declares and adds a pre-existing instance to this scope's cache.
     * This is useful for binding instances that are not created by the DI framework itself, such as Android's `Context`.
     *
     * @param qualifier The [Qualifier] to associate with the instance.
     * @param instance The pre-existing object to be stored in the scope.
     * @throws IllegalStateException if a dependency with the same qualifier already exists.
     */
    fun declare(
        qualifier: Qualifier,
        instance: Any,
    )

    /**
     * Registers one or more [DependencyModule]s into the scope.
     * This populates the scope with the necessary factories to create dependency instances.
     *
     * @param modules A variable number of [DependencyModule]s to register.
     */
    fun registerFactory(vararg modules: DependencyModule)

    /**
     * Closes this scope and clears all of its cached singleton instances.
     * This is typically called when the lifecycle associated with this scope ends (e.g., `Activity.onDestroy()`).
     * Note: This does not close parent or child scopes.
     */
    fun closeAll()
}
