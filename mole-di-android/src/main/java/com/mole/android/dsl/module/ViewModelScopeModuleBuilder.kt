package com.mole.android.dsl.module

import android.app.Activity
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.annotation.MoleInternalApi
import com.mole.core.dsl.AbstractModuleBuilder
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.Scope

/**
 * A specialized [AbstractModuleBuilder] for defining dependencies within a ViewModel-specific scope.
 * This builder is used with the `viewModelScope` DSL function.
 *
 * @property scope The current [Scope] in which the ViewModel-level dependencies are being defined.
 */
@OptIn(MoleInternalApi::class)
class ViewModelScopeModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder() {
    /**
     * Defines a nested, standard `ActivityScope` within the ViewModel scope.
     * This can be useful for dependencies that need the Activity context and should be tied to the Activity's lifecycle.
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
            ComplexQualifier(
                qualifier,
                AndroidScopeKeys.ACTIVITY,
            ),
            { ActivityScopeModuleBuilder(this) },
            block,
        )
    }
}
