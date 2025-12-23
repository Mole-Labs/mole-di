package com.mole.core.dsl

import com.mole.core.scope.ScopeImpl

@ModuleBuilderDSL
class DependencyModuleBuilder(
    override val scope: ScopeImpl,
) : AbstractModuleBuilder()
