package com.mole.android.scope

import com.mole.core.dsl.ScopePathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.get

/**
 * Lazily injects a dependency from an [AndroidScopes] instance.
 *
 * @param T The type of the dependency to inject.
 * @param qualifier An optional [Qualifier] to distinguish between dependencies of the same type.
 * @param mode The [LazyThreadSafetyMode] for the lazy delegate.
 * @return A [Lazy] delegate that provides the dependency instance [T].
 */
@JvmName("injectAndroidScope")
inline fun <reified T> Lazy<AndroidScopes>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { value.scope.get(qualifier) as T }

/**
 * Lazily injects a dependency from a sub-scope specified by a path.
 *
 * @param T The type of the dependency to inject.
 * @param qualifier An optional [Qualifier] to distinguish between dependencies of the same type.
 * @param mode The [LazyThreadSafetyMode] for the lazy delegate.
 * @param pathBuilder A DSL block to define the path to the target scope.
 * @return A [Lazy] delegate that provides the dependency instance [T].
 */
@JvmName("injectAndroidScope")
inline fun <reified T> Lazy<AndroidScopes>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Lazy<T> = lazy(mode) { value.scope.get(qualifier, pathBuilder) as T }
