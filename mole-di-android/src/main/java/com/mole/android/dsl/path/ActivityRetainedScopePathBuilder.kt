package com.mole.android.dsl.path

import com.mole.core.dsl.AbstractPathBuilder
import com.mole.core.path.Path

/**
 * A specialized [AbstractPathBuilder] for creating a scope resolution [Path] starting from an ActivityRetained scope.
 * This builder is more limited as it typically represents a higher-level scope in the activity-related hierarchy.
 *
 * @property path The [Path] object being constructed.
 */
class ActivityRetainedScopePathBuilder(
    override val path: Path,
) : AbstractPathBuilder<ActivityRetainedScopePathBuilder>()
