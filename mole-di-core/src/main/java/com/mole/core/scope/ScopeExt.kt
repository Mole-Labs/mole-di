package com.mole.core.scope

import com.mole.core.dsl.ScopePathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

fun DefaultScope.subScope(pathBuilder: ScopePathBuilder.() -> Path): DefaultScope {
    val path = pathBuilder(ScopePathBuilder(Path()))
    return resolvePath(path)
}

@JvmName("inlineGet")
inline fun <reified T : Any> DefaultScope.get(
    qualifier: Qualifier = TypeQualifier(T::class),
    noinline pathBuilder: ScopePathBuilder.() -> Path = { Path() },
): Any = resolvePath(pathBuilder(ScopePathBuilder(Path()))).get(qualifier)

fun DefaultScope.get(
    qualifier: Qualifier,
    pathBuilder: ScopePathBuilder.() -> Path,
): Any = resolvePath(pathBuilder(ScopePathBuilder(Path()))).get(qualifier)

inline fun <reified T> Lazy<DefaultScope>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { value.get(qualifier) as T }

inline fun <reified T> Lazy<DefaultScope>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Lazy<T> = lazy(mode) { value.get(qualifier, pathBuilder) as T }
