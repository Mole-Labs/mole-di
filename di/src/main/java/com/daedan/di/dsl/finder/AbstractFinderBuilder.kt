package com.daedan.di.dsl.finder

import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.finder.Finder
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@ModuleBuilderDSL
abstract class AbstractFinderBuilder {
    inline fun <reified T : Any> find(
        qualifier: Qualifier = TypeQualifier(T::class),
        noinline block: ScopeFinderBuilder.() -> Finder = { Finder(qualifier) },
    ): Finder =
        Finder(
            qualifier = qualifier,
            next = block(ScopeFinderBuilder()),
        )
}
