package com.mole.android.dsl.path

import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.dsl.AbstractPathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

/**
 * A specialized [AbstractPathBuilder] for creating a scope resolution [Path] starting from a ViewModel scope.
 * It allows navigation to an `activityRetainedScope` from a ViewModel scope.
 *
 * @property path The [Path] object being constructed.
 */
class ViewModelScopePathBuilder(
    override val path: Path,
) : AbstractPathBuilder<ViewModelScopePathBuilder>() {
    /**
     * Specifies an `ActivityRetainedScope` as the next step in the path.
     *
     * @param T The Activity class associated with the scope.
     * @param qualifier An optional, explicit [Qualifier] for the ActivityRetained scope.
     * @return An [ActivityRetainedScopeToken] to continue the path-building chain.
     */
    inline fun <reified T : Any> activityRetainedScope(qualifier: Qualifier = TypeQualifier(T::class)) =
        ActivityRetainedScopeToken(
            ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
        )

    /**
     * Appends an `ActivityRetainedScope` to the path and returns a builder for that scope.
     */
    infix fun of(qualifier: ActivityRetainedScopeToken): ActivityRetainedScopePathBuilder {
        path.append(qualifier.value)
        return ActivityRetainedScopePathBuilder(path)
    }
}
