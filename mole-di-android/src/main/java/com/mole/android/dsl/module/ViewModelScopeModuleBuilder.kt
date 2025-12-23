package com.mole.android.dsl.module

import android.app.Activity
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.annotation.MoleInternalApi
import com.mole.core.dsl.AbstractModuleBuilder
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.ScopeImpl

@OptIn(MoleInternalApi::class)
class ViewModelScopeModuleBuilder(
    override val scope: ScopeImpl,
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
