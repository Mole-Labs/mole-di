package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import com.mole.core.scope.ScopeImpl

class ScopeDependencyFactory(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> ScopeImpl,
) : DependencyFactory<ScopeImpl>
