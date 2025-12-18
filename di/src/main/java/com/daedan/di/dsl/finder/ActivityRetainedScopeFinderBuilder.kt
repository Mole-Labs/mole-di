package com.daedan.di.dsl.finder

import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.finder.Finder
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@ModuleBuilderDSL
class ActivityRetainedScopeFinderBuilder : AbstractFinderBuilder() {
    inline fun <reified T : Any> findActivityScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ActivityScopeFinderBuilder.() -> Finder = { Finder(qualifier) },
    ): Finder =
        Finder(
            qualifier = qualifier,
            next = block(ActivityScopeFinderBuilder()),
        )

    inline fun <reified T : Any> findViewModelScope(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ViewModelScopeFinderBuilder.() -> Finder = { Finder(qualifier) },
    ): Finder =
        Finder(
            qualifier = qualifier,
            next = block(ViewModelScopeFinderBuilder()),
        )
}
