package com.daedan.di.module

import com.daedan.di.Scope
import com.daedan.di.qualifier.CreateRule
import com.daedan.di.qualifier.Qualifier

class ScopeDependencyFactory(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> Scope,
) : DependencyFactory<Scope>
