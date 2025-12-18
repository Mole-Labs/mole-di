package com.daedan.di.dsl

import com.daedan.di.Scope
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

class ViewModelScopeModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder() {
    inline fun <reified T : Any> activityScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(qualifier, { ActivityScopeModuleBuilder(this) }, block)
    }
}
