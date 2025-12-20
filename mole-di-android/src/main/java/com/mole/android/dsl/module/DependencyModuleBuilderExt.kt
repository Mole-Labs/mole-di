package com.mole.android.dsl.module

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.annotation.MoleInternalApi
import com.mole.core.dsl.DependencyModuleBuilder
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

@OptIn(MoleInternalApi::class)
inline fun <reified T : Activity> DependencyModuleBuilder.activityScope(
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

@OptIn(MoleInternalApi::class)
inline fun <reified T : Activity> DependencyModuleBuilder.activityRetainedScope(
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

@OptIn(MoleInternalApi::class)
inline fun <reified T : ViewModel> DependencyModuleBuilder.viewModelScope(
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
