package com.mole.android.dsl.module

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.android.dsl.ModuleBuilderDSL
import com.mole.android.qualifier.ComplexQualifier
import com.mole.android.qualifier.Qualifier
import com.mole.android.qualifier.TypeQualifier
import com.mole.android.scope.Scope

@ModuleBuilderDSL
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
