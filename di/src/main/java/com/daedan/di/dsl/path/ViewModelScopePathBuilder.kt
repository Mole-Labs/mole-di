package com.daedan.di.dsl.path

import com.daedan.di.dsl.AndroidScopeKeys
import com.daedan.di.path.Path
import com.daedan.di.qualifier.ComplexQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

class ViewModelScopePathBuilder(
    override val path: Path,
) : AbstractPathBuilder() {
    inline fun <reified T : Any> activityRetainedScope(qualifier: Qualifier = TypeQualifier(T::class)) =
        ActivityRetainedScopeToken(
            ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
        )

    infix fun of(qualifier: ActivityRetainedScopeToken): ActivityRetainedScopePathBuilder {
        path.append(qualifier.value)
        return ActivityRetainedScopePathBuilder(path)
    }
}
