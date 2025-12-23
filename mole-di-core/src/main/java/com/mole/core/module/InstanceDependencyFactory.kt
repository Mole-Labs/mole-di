package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier

/**
 * A concrete implementation of [DependencyFactory] for creating standard object instances.
 * This is the most common factory, used for `single` and `factory` definitions.
 *
 * @param T The type of the dependency this factory creates.
 * @property qualifier The [Qualifier] that uniquely identifies the dependency.
 * @property createRule The [CreateRule] determining the instance's lifecycle (singleton or factory).
 * @property create The lambda function to create the instance.
 */
class InstanceDependencyFactory<T : Any>(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> T,
) : DependencyFactory<T>
