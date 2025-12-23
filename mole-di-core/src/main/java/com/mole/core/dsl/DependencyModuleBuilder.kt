package com.mole.core.dsl

import com.mole.core.scope.Scope

/**
 * A concrete implementation of [AbstractModuleBuilder] for defining a standard module.
 * This is the primary entry point for the module definition DSL.
 *
 * @property scope The current [Scope] in which the module is being built.
 */
@ModuleBuilderDSL
class DependencyModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder()
