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

/**
 * A specialized [AbstractModuleBuilder] for defining dependencies within an activity-retained scope.
 * This builder allows for nesting `activityScope` and `viewModelScope` inside a scope that survives configuration changes.
 *
 * @property scope The current activity-retained [Scope] in which the dependencies are being defined.
 */
@ModuleBuilderDSL
@OptIn(MoleInternalApi::class)
class ActivityRetainedScopeModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder() {
    /**
     * Defines a nested, standard `ActivityScope` within the retained scope.
     * Dependencies defined here will be tied to the Activity's lifecycle and will be recreated if the Activity is recreated.
     *
     * @param T The Activity class associated with this scope.
     * @param qualifier An optional [Qualifier] for the activity scope.
     * @param block A DSL block to define dependencies for the activity scope.
     */
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

    /**
     * Defines a nested `ViewModelScope` within the retained scope.
     *
     * @param T The ViewModel class associated with this scope.
     * @param qualifier An optional [Qualifier] for the ViewModel scope.
     * @param block A DSL block to define dependencies for the ViewModel scope.
     */
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
