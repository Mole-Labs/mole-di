package com.mole.core.dsl

import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

/**
 * An abstract base class for creating a scope resolution [Path] using a DSL.
 * It provides a fluent API for building a path to a nested scope.
 *
 * Example Usage:
 * ```
 * subScope { find of scope<ViewModelScope>() of scope<ActivityScope>() of Root }
 * ```
 *
 * @param T The concrete type of the path builder, used for fluent method chaining.
 */
@ModuleBuilderDSL
abstract class AbstractPathBuilder<T : AbstractPathBuilder<T>> {
    /**
     * The [Path] object being constructed by the builder.
     */
    abstract val path: Path

    /**
     * A starting point for the DSL chain. Returns the concrete builder instance.
     */
    @Suppress("UNCHECKED_CAST")
    val find: T get() = this as T

    /**
     * Creates a [TypeQualifier] for the given type [T], to be used within the path.
     *
     * @param T The class type to create a qualifier for.
     * @param qualifier An optional, explicit [Qualifier] to use instead of creating one from the type.
     * @return A [Qualifier] for the specified type.
     */
    inline fun <reified T : Any> scope(qualifier: Qualifier = TypeQualifier(T::class)): Qualifier = qualifier

    /**
     * Appends a [Qualifier] to the current path, representing a step down into a child scope.
     * The `of` is used as an infix operator to create a natural, readable DSL.
     *
     * @param qualifier The [Qualifier] of the child scope to navigate to.
     * @return The concrete builder instance [T] for further chaining.
     */
    @Suppress("UNCHECKED_CAST")
    infix fun of(qualifier: Qualifier): T {
        path.append(qualifier)
        return this as T
    }

    /**
     * Finalizes the path construction, returning the completed [Path] object.
     * This should be the last call in the DSL chain, terminating at the [Root] of the scope hierarchy.
     *
     * @param root The [Root] object, acting as a terminal symbol for the path.
     * @return The fully constructed [Path].
     */
    infix fun of(root: Root): Path = path
}
