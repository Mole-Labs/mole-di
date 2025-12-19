package com.daedan.di.dsl.path

import com.daedan.di.dsl.AndroidScopeKeys
import com.daedan.di.path.Path
import com.daedan.di.qualifier.ComplexQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

class ScopePathBuilder(
    override val path: Path,
) : AbstractPathBuilder<ScopePathBuilder>() {
    inline fun <reified T : Any> viewModelScope(qualifier: Qualifier = TypeQualifier(T::class)) =
        ViewModelScopeToken(
            ComplexQualifier(qualifier, AndroidScopeKeys.VIEWMODEL),
        )

    inline fun <reified T : Any> activityRetainedScope(qualifier: Qualifier = TypeQualifier(T::class)) =
        ActivityRetainedScopeToken(
            ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
        )

    inline fun <reified T : Any> activityScope(qualifier: Qualifier = TypeQualifier(T::class)) =
        ActivityScopeToken(
            ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
        )

    infix fun of(qualifier: ActivityRetainedScopeToken): ActivityRetainedScopePathBuilder {
        path.append(qualifier.value)
        return ActivityRetainedScopePathBuilder(path)
    }

    infix fun of(qualifier: ActivityScopeToken): ActivityScopePathBuilder {
        path.append(qualifier.value)
        return ActivityScopePathBuilder(path)
    }

    infix fun of(qualifier: ViewModelScopeToken): ViewModelScopePathBuilder {
        path.append(qualifier.value)
        return ViewModelScopePathBuilder(path)
    }
}
