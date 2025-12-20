package com.mole.android.dsl.module

import androidx.lifecycle.ViewModel
import com.mole.core.dsl.AbstractModuleBuilder
import com.mole.core.dsl.ModuleBuilderDSL
import com.mole.core.module.InstanceDependencyFactory
import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

@ModuleBuilderDSL
inline fun <reified T : ViewModel> AbstractModuleBuilder.viewModel(
    qualifier: Qualifier = TypeQualifier(T::class),
    noinline create: () -> T,
) {
    val createRule = CreateRule.FACTORY
    factories.add(InstanceDependencyFactory(qualifier, createRule, create))
}
