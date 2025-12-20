package com.mole.android.dsl.module

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.annotation.MoleInternalApi
import com.mole.core.dsl.AbstractModuleBuilder
import com.mole.core.dsl.ModuleBuilderDSL
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.Scope

@ModuleBuilderDSL
@OptIn(MoleInternalApi::class)
class ActivityRetainedScopeModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder() {
    inline fun <reified T : Activity> activityScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(
            ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY),
            { ActivityScopeModuleBuilder(this) },
            block,
        )
    }

    inline fun <reified T : ViewModel> viewModelScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ViewModelScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(
            ComplexQualifier(
                qualifier,
                AndroidScopeKeys.VIEWMODEL,
            ),
            { ViewModelScopeModuleBuilder(this) },
            block,
        )
    }
}
