package com.mole.android.dsl.path

import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.dsl.AbstractPathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

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
