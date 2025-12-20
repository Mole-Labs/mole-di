package com.mole.core.dsl

import com.mole.core.scope.Scope

@ModuleBuilderDSL
class DependencyModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder()
