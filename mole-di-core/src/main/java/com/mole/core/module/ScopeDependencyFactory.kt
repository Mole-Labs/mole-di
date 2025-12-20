package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import com.mole.core.scope.Scope

class ScopeDependencyFactory(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> Scope,
) : DependencyFactory<Scope>
