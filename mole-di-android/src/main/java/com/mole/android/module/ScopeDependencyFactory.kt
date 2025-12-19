package com.mole.android.module

import com.mole.android.qualifier.CreateRule
import com.mole.android.qualifier.Qualifier
import com.mole.android.scope.Scope

class ScopeDependencyFactory(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> Scope,
) : DependencyFactory<Scope>
