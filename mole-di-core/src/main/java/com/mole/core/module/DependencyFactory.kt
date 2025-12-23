package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier

/**
 * A sealed interface representing a factory for creating dependency instances.
 * A factory is a recipe that tells the DI container how to create an object of a specific type [T].
 *
 * @param T The type of the dependency this factory creates.
 */
sealed interface DependencyFactory<T : Any> {
    /**
     * The [Qualifier] that uniquely identifies the dependency this factory produces.
     */
    val qualifier: Qualifier

    /**
     * The [CreateRule] that determines the lifecycle of the created instance (e.g., singleton or factory).
     */
    val createRule: CreateRule

    /**
     * The lambda function that contains the logic for creating the dependency instance.
     */
    val create: () -> T

    /**
     * Invokes the [create] lambda to produce a new instance of the dependency.
     */
    operator fun invoke() = create.invoke()
}
