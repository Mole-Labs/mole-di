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

@JvmName("inlineGet")
inline fun <reified T : Any> Scope.get(
    qualifier: Qualifier = TypeQualifier(T::class),
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Any {
    val scope = subScope(pathBuilder)
    return scope.get(qualifier)
}

fun Scope.get(
    qualifier: Qualifier,
    pathBuilder: ScopePathBuilder.() -> Path,
): Any {
    val scope = subScope(pathBuilder)
    return scope.get(qualifier)
}

inline fun <reified T> Lazy<Scope>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { value.get(qualifier) as T }

inline fun <reified T> Lazy<Scope>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Lazy<T> = lazy(mode) { value.get(qualifier, pathBuilder) as T }
