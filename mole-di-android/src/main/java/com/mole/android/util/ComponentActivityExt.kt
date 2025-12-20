package com.mole.android.util

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.android.dsl.path.ActivityRetainedScopePathBuilder
import com.mole.android.dsl.path.ActivityScopePathBuilder
import com.mole.android.dsl.path.ViewModelScopePathBuilder
import com.mole.android.scope.AndroidScopes
import com.mole.core.DiComponent
import com.mole.core.dsl.Root
import com.mole.core.path.Path
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

// --- Activity Scope ---
fun ComponentActivity.activityScope(
    pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityScope> =
    createScopeLazy(
        initialQualifier = ComplexQualifier(TypeQualifier(this::class), AndroidScopeKeys.ACTIVITY),
        builderFactory = ::ActivityScopePathBuilder,
        pathBuilder = pathBuilder,
        onResolved = { initialize(it) },
    )

@JvmName("inlineActivityScope")
inline fun <reified T : Activity> ComponentActivity.activityScope(
    noinline pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityScope> = activityScope(pathBuilder)

// --- ViewModel Scope ---
inline fun <reified T : ViewModel> ComponentActivity.viewModelScope(
    noinline pathBuilder: ViewModelScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ViewModelScope> =
    createScopeLazy(
        initialQualifier = ComplexQualifier(TypeQualifier(T::class), AndroidScopeKeys.VIEWMODEL),
        builderFactory = ::ViewModelScopePathBuilder,
        pathBuilder = pathBuilder,
    )

// --- Activity Retained Scope ---
fun ComponentActivity.activityRetainedScope(
    pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityRetainedScope> =
    createScopeLazy(
        initialQualifier =
            ComplexQualifier(
                TypeQualifier(this::class),
                AndroidScopeKeys.ACTIVITY_RETAINED,
            ),
        builderFactory = ::ActivityRetainedScopePathBuilder,
        pathBuilder = pathBuilder,
        onResolved = { registerActivityRetainedLifecycle(it) },
    )

@JvmName("inlineActivityRetainedScope")
inline fun <reified T : Activity> ComponentActivity.activityRetainedScope(
    noinline pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityRetainedScope> = activityRetainedScope(pathBuilder)

@MainThread
fun ComponentActivity.getRootScope() = (applicationContext as? DiComponent)?.rootScope ?: error("DiComponent의 하위 타입이 아닙니다. ")

inline fun <reified T> ComponentActivity.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { getRootScope().get(qualifier) as T }
