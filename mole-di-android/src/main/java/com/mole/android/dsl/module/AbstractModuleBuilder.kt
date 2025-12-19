package com.mole.android.dsl.module

import androidx.lifecycle.ViewModel
import com.mole.android.dsl.ModuleBuilderDSL
import com.mole.android.module.DependencyFactory
import com.mole.android.module.DependencyModule
import com.mole.android.module.InstanceDependencyFactory
import com.mole.android.module.ScopeDependencyFactory
import com.mole.android.qualifier.CreateRule
import com.mole.android.qualifier.Qualifier
import com.mole.android.qualifier.TypeQualifier
import com.mole.android.scope.Scope

@ModuleBuilderDSL
abstract class AbstractModuleBuilder {
    abstract val scope: Scope

    val factories = mutableListOf<DependencyFactory<*>>()

    inline fun <reified T : ViewModel> viewModel(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline create: () -> T,
    ) {
        val createRule = CreateRule.FACTORY
        factories.add(InstanceDependencyFactory(qualifier, createRule, create))
    }

    inline fun <reified T : Any> factory(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline create: () -> T,
    ) {
        val createRule = CreateRule.FACTORY
        factories.add(InstanceDependencyFactory(qualifier, createRule, create))
    }

    inline fun <reified T : Any> single(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline create: () -> T,
    ) {
        val createRule = CreateRule.SINGLE
        factories.add(InstanceDependencyFactory(qualifier, createRule, create))
    }

    inline fun <reified T : Any> scope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: DependencyModuleBuilder.() -> Unit,
    ) {
        baseScope(
            qualifier,
            { DependencyModuleBuilder(this) },
            block,
        )
    }

    @PublishedApi
    internal fun <T : AbstractModuleBuilder> baseScope(
        qualifier: Qualifier,
        builderGenerator: Scope.() -> T,
        block: T.() -> Unit,
    ) {
        val createRule = CreateRule.SINGLE
        factories.add(
            ScopeDependencyFactory(
                qualifier,
                createRule,
            ) {
                Scope(qualifier, scope).apply {
                    val builder = builderGenerator(this)
                    block(builder)
                    val modules = builder.build()
                    registerFactory(modules)
                }
            },
        )
    }

    inline fun <reified T : Any> get(qualifier: Qualifier = TypeQualifier(T::class)): T = scope.get(qualifier) as T

    fun build(): DependencyModule = DependencyModule(factories)
}
