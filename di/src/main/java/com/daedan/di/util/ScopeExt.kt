package com.daedan.di.util

import com.daedan.di.Scope
import com.daedan.di.dsl.path.ScopePathBuilder
import com.daedan.di.path.Path
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

fun Scope.subScope(pathBuilder: ScopePathBuilder.() -> Path): Scope {
    val path = pathBuilder(ScopePathBuilder(Path()))
    var scope: Scope = this
    for (qualifier in path.order) {
        scope = scope.getSubScope(qualifier)
    }
    return scope
}

inline fun <reified T : Any> Scope.get(
    qualifier: Qualifier = TypeQualifier(T::class),
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Any {
    val scope = subScope(pathBuilder)
    return scope.get(qualifier)
}
