package com.mole.android.dsl.path

import com.mole.android.dsl.AndroidScopeKeys
import com.mole.core.dsl.ScopePathBuilder
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

inline fun <reified T : Any> ScopePathBuilder.viewModelScope(qualifier: Qualifier = TypeQualifier(T::class)) =
    ViewModelScopeToken(
        ComplexQualifier(qualifier, AndroidScopeKeys.VIEWMODEL),
    )

inline fun <reified T : Any> ScopePathBuilder.activityRetainedScope(
    qualifier: Qualifier =
        TypeQualifier(
            T::class,
        ),
) = ActivityRetainedScopeToken(
    ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
)

inline fun <reified T : Any> ScopePathBuilder.activityScope(qualifier: Qualifier = TypeQualifier(T::class)) =
    ActivityScopeToken(
        ComplexQualifier(qualifier, AndroidScopeKeys.ACTIVITY_RETAINED),
    )

infix fun ScopePathBuilder.of(qualifier: ActivityRetainedScopeToken): ActivityRetainedScopePathBuilder {
    path.append(qualifier.value)
    return ActivityRetainedScopePathBuilder(path)
}

infix fun ScopePathBuilder.of(qualifier: ActivityScopeToken): ActivityScopePathBuilder {
    path.append(qualifier.value)
    return ActivityScopePathBuilder(path)
}

infix fun ScopePathBuilder.of(qualifier: ViewModelScopeToken): ViewModelScopePathBuilder {
    path.append(qualifier.value)
    return ViewModelScopePathBuilder(path)
}
