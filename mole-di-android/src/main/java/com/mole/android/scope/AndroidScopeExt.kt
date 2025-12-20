package com.mole.android.scope

import com.mole.core.dsl.ScopePathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.get

@JvmName("injectAndroidScope")
inline fun <reified T> Lazy<AndroidScopes>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { value.scope.get(qualifier) as T }

@JvmName("injectAndroidScope")
inline fun <reified T> Lazy<AndroidScopes>.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline pathBuilder: ScopePathBuilder.() -> Path,
): Lazy<T> = lazy(mode) { value.scope.get(qualifier, pathBuilder) as T }
