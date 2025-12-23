package com.mole.core.dsl

import com.mole.core.annotation.MoleInternalApi
import com.mole.core.module.DependencyFactory
import com.mole.core.module.DependencyModule
import com.mole.core.module.InstanceDependencyFactory
import com.mole.core.module.ScopeDependencyFactory
import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.ScopeImpl

@ModuleBuilderDSL
@OptIn(MoleInternalApi::class)
abstract class AbstractModuleBuilder {
    abstract val scope: ScopeImpl

    val factories = mutableListOf<DependencyFactory<*>>()

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

    @MoleInternalApi
    fun <T : AbstractModuleBuilder> baseScope(
        qualifier: Qualifier,
        builderGenerator: ScopeImpl.() -> T,
        block: T.() -> Unit,
    ) {
        val createRule = CreateRule.SINGLE
        factories.add(
            ScopeDependencyFactory(
                qualifier,
                createRule,
            ) {
                ScopeImpl(qualifier, scope).apply {
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
