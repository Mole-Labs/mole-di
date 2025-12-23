package com.mole.core.dsl

import com.mole.core.annotation.MoleInternalApi
import com.mole.core.module.DependencyFactory
import com.mole.core.module.DependencyModule
import com.mole.core.module.InstanceDependencyFactory
import com.mole.core.module.ScopeDependencyFactory
import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.DefaultScope
import com.mole.core.scope.Scope

/**
 * An abstract base class for creating a [DependencyModule] using a DSL.
 * It provides the core functions for defining dependencies, such as `single`, `factory`, and `scope`.
 * This class is not meant to be used directly but should be extended by concrete builder implementations.
 */
@ModuleBuilderDSL
@OptIn(MoleInternalApi::class)
abstract class AbstractModuleBuilder {
    /**
     * The current [Scope] in which the dependencies are being defined.
     */
    abstract val scope: Scope

    /**
     * A mutable list that collects all the [DependencyFactory] instances created by the builder.
     */
    val factories = mutableListOf<DependencyFactory<*>>()

    /**
     * Defines a dependency that will have a new instance created every time it is requested.
     *
     * @param T The type of the dependency.
     * @param qualifier An optional [Qualifier] to uniquely identify this dependency.
     * @param create A lambda function that provides an instance of the dependency.
     */
    inline fun <reified T : Any> factory(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline create: () -> T,
    ) {
        val createRule = CreateRule.FACTORY
        factories.add(InstanceDependencyFactory(qualifier, createRule, create))
    }

    /**
     * Defines a singleton dependency that will have only one instance created and shared throughout the scope's lifecycle.
     *
     * @param T The type of the dependency.
     * @param qualifier An optional [Qualifier] to uniquely identify this dependency.
     * @param create A lambda function that provides the single instance of the dependency.
     */
    inline fun <reified T : Any> single(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline create: () -> T,
    ) {
        val createRule = CreateRule.SINGLE
        factories.add(InstanceDependencyFactory(qualifier, createRule, create))
    }

    /**
     * Defines a nested scope within the current module.
     *
     * @param T The type to be used as a base for the scope's [TypeQualifier].
     * @param qualifier An optional [Qualifier] to uniquely identify this sub-scope.
     * @param block A lambda with a [DependencyModuleBuilder] receiver to define dependencies within the nested scope.
     */
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

    /**
     * An internal-use function to create and register a sub-scope factory.
     *
     * @param qualifier The qualifier for the new scope.
     * @param builderGenerator A function that creates a new builder for the sub-scope.
     * @param block The DSL block for defining dependencies in the sub-scope.
     */
    @MoleInternalApi
    fun <T : AbstractModuleBuilder> baseScope(
        qualifier: Qualifier,
        builderGenerator: DefaultScope.() -> T,
        block: T.() -> Unit,
    ) {
        val createRule = CreateRule.SINGLE
        factories.add(
            ScopeDependencyFactory(
                qualifier,
                createRule,
            ) {
                val newScope = DefaultScope(qualifier, scope as DefaultScope)
                val builder = builderGenerator(newScope)
                block(builder)
                val modules = builder.build()
                newScope.registerFactory(modules)
                newScope
            },
        )
    }

    /**
     * A helper function to resolve a dependency from the current scope.
     * This is intended to be used within the `create` lambda of other dependency definitions.
     *
     * @param T The type of the dependency to retrieve.
     * @param qualifier An optional [Qualifier] to specify a particular dependency.
     * @return An instance of the requested dependency [T].
     */
    inline fun <reified T : Any> get(qualifier: Qualifier = TypeQualifier(T::class)): T = scope.get(qualifier) as T

    /**
     * Builds and returns the [DependencyModule] containing all the defined factories.
     *
     * @return The configured [DependencyModule].
     */
    fun build(): DependencyModule = DependencyModule(factories)
}
