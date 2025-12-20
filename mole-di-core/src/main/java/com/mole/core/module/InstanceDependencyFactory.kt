package com.mole.core.module

import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier

class InstanceDependencyFactory<T : Any>(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> T,
) : DependencyFactory<T>
