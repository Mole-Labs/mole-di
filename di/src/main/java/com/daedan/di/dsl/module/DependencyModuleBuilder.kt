package com.daedan.di.dsl.module

import com.daedan.di.Scope
import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@ModuleBuilderDSL
class DependencyModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder() {
    inline fun <reified T : Any> activityScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(qualifier, { ActivityScopeModuleBuilder(this) }, block)
    }

    inline fun <reified T : Any> activityRetainedScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityRetainedScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(qualifier, { ActivityRetainedScopeModuleBuilder(this) }, block)
    }

    inline fun <reified T : Any> viewModelScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ViewModelScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(qualifier, { ViewModelScopeModuleBuilder(this) }, block)
    }
}
