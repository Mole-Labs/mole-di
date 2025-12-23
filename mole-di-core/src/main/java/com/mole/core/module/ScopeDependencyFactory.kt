package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import com.mole.core.scope.DefaultScope
import com.mole.core.scope.Scope

/**
 * A specialized [DependencyFactory] for creating nested [Scope] instances.
 * This factory is used by the `scope<T> { ... }` DSL function to define a sub-scope within a module.
 *
 * @property qualifier The [Qualifier] that uniquely identifies the sub-scope.
 * @property createRule The [CreateRule] for the scope, which is typically [CreateRule.SINGLE] to ensure the scope instance is a singleton within its parent.
 * @property create The lambda function that creates and configures the new [Scope] instance.
 */
class ScopeDependencyFactory(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> DefaultScope,
) : DependencyFactory<DefaultScope>
