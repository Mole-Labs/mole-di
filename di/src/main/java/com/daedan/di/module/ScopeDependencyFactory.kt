package com.daedan.di.module

import com.daedan.di.qualifier.CreateRule
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.scope.Scope

class ScopeDependencyFactory(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> Scope,
) : DependencyFactory<Scope>
