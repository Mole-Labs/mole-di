package com.daedan.di.module

import com.daedan.di.qualifier.CreateRule
import com.daedan.di.qualifier.Qualifier

class InstanceDependencyFactory<T : Any>(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> T,
) : DependencyFactory<T>
