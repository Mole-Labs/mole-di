package com.daedan.di.dsl.module

import androidx.lifecycle.ViewModel
import com.daedan.di.Scope
import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.module.DependencyFactory
import com.daedan.di.module.DependencyModule
import com.daedan.di.module.InstanceDependencyFactory
import com.daedan.di.module.ScopeDependencyFactory
import com.daedan.di.qualifier.CreateRule
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

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
