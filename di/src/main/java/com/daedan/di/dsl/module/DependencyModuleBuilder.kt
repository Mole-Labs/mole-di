package com.daedan.di.dsl.module

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.daedan.di.dsl.AndroidScopeKeys
import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.qualifier.ComplexQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier
import com.daedan.di.scope.Scope

@ModuleBuilderDSL
class DependencyModuleBuilder(
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

    inline fun <reified T : Activity> activityRetainedScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityRetainedScopeModuleBuilder.() -> Unit,
    ) {
        baseScope(
            ComplexQualifier(
                qualifier,
                AndroidScopeKeys.ACTIVITY_RETAINED,
            ),
            { ActivityRetainedScopeModuleBuilder(this) },
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
