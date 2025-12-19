package com.mole.android.dsl.path

import com.mole.android.dsl.AndroidScopeKeys
import com.mole.android.path.Path
import com.mole.android.qualifier.ComplexQualifier
import com.mole.android.qualifier.Qualifier
import com.mole.android.qualifier.TypeQualifier

class ViewModelScopePathBuilder(
    override val path: Path,
) : AbstractPathBuilder<ViewModelScopePathBuilder>() {
    inline fun <reified T : Any> activityRetainedScope(qualifier: Qualifier = TypeQualifier(T::class)) =
        ActivityRetainedScopeToken(
            ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
        )

    infix fun of(qualifier: ActivityRetainedScopeToken): ActivityRetainedScopePathBuilder {
        path.append(qualifier.value)
        return ActivityRetainedScopePathBuilder(path)
    }
}
