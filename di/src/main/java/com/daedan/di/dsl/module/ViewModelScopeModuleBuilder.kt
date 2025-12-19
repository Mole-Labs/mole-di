package com.daedan.di.dsl.module

import android.app.Activity
import com.daedan.di.dsl.AndroidScopeKeys
import com.daedan.di.qualifier.ComplexQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier
import com.daedan.di.scope.Scope

class ViewModelScopeModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder() {
    inline fun <reified T : Activity> activityScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(
            ComplexQualifier(
                qualifier,
                AndroidScopeKeys.ACTIVITY,
            ),
            { ActivityScopeModuleBuilder(this) },
            block,
        )
    }
}
