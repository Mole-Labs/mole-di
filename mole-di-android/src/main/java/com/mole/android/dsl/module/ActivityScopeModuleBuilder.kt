package com.mole.android.dsl.module

import com.mole.core.dsl.AbstractModuleBuilder
import com.mole.core.scope.Scope

/**
 * A specialized [AbstractModuleBuilder] for defining dependencies within an Activity-specific scope.
 * This builder is used with the `activityScope` DSL function.
 *
 * @property scope The current [Scope] in which the activity-level dependencies are being defined.
 */
class ActivityScopeModuleBuilder(
    override val scope: Scope,
) : AbstractModuleBuilder()
