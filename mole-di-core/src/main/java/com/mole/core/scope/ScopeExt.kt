package com.mole.core.scope

import com.mole.core.dsl.ScopePathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

fun Scope.subScope(pathBuilder: ScopePathBuilder.() -> Path): Scope {
    val path = pathBuilder(ScopePathBuilder(Path()))
    return resolvePath(path)
}

@JvmName("inlineGet")
inline fun <reified T : Any> Scope.get(
    qualifier: Qualifier = TypeQualifier(T::class),
    noinline pathBuilder: ScopePathBuilder.() -> Path = { Path() },
): Any = resolvePath(pathBuilder(ScopePathBuilder(Path()))).get(qualifier)

fun Scope.get(
    qualifier: Qualifier,
    pathBuilder: ScopePathBuilder.() -> Path,
): Any = resolvePath(pathBuilder(ScopePathBuilder(Path()))).get(qualifier)

inline fun <reified T> Lazy<Scope>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { value.get(qualifier) as T }

inline fun <reified T> Lazy<Scope>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Lazy<T> = lazy(mode) { value.get(qualifier, pathBuilder) as T }
