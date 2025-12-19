package com.daedan.di.util

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import com.daedan.di.DiComponent
import com.daedan.di.Scope
import com.daedan.di.dsl.AndroidScopeKeys
import com.daedan.di.dsl.path.ActivityRetainedScopePathBuilder
import com.daedan.di.dsl.path.ActivityScopePathBuilder
import com.daedan.di.dsl.path.Root
import com.daedan.di.dsl.path.ViewModelScopePathBuilder
import com.daedan.di.path.Path
import com.daedan.di.qualifier.ComplexQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

// --- Activity Scope ---
fun ComponentActivity.activityScope(pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root }): Lazy<Scope> =
    createScopeLazy(
        initialQualifier = ComplexQualifier(TypeQualifier(this::class), AndroidScopeKeys.ACTIVITY),
        builderFactory = ::ActivityScopePathBuilder,
        pathBuilder = pathBuilder,
        onResolved = { initialize(it) },
    )

@JvmName("inlineActivityScope")
inline fun <reified T : Activity> ComponentActivity.activityScope(
    noinline pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root },
): Lazy<Scope> = activityScope(pathBuilder)

// --- ViewModel Scope ---
inline fun <reified T : ViewModel> ComponentActivity.viewModelScope(
    noinline pathBuilder: ViewModelScopePathBuilder.() -> Path = { find of Root },
): Lazy<Scope> =
    createScopeLazy(
        initialQualifier = ComplexQualifier(TypeQualifier(T::class), AndroidScopeKeys.VIEWMODEL),
        builderFactory = ::ViewModelScopePathBuilder,
        pathBuilder = pathBuilder,
    )

// --- Activity Retained Scope ---
fun ComponentActivity.activityRetainedScope(pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = { find of Root }): Lazy<Scope> =
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
): Lazy<Scope> = activityRetainedScope(pathBuilder)

@MainThread
fun ComponentActivity.getRootScope() = (applicationContext as? DiComponent)?.rootScope ?: error("DiComponent의 하위 타입이 아닙니다. ")

inline fun <reified T> ComponentActivity.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { getRootScope().get(qualifier) as T }
