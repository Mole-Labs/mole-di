package com.mole.android.module

import com.mole.android.qualifier.CreateRule
import com.mole.android.qualifier.Qualifier

class InstanceDependencyFactory<T : Any>(
    override val qualifier: Qualifier,
    override val createRule: CreateRule,
    override val create: () -> T,
) : DependencyFactory<T>
