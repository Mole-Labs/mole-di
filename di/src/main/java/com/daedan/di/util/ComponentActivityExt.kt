package com.daedan.di.util

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daedan.di.DiComponent
import com.daedan.di.Scope
import com.daedan.di.dsl.AndroidScopeKeys
import com.daedan.di.dsl.path.ActivityRetainedScopePathBuilder
import com.daedan.di.dsl.path.ActivityScopePathBuilder
import com.daedan.di.dsl.path.ViewModelScopePathBuilder
import com.daedan.di.path.Path
import com.daedan.di.qualifier.ComplexQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

fun ComponentActivity.activityScope(
    pathBuilder: ActivityScopePathBuilder.() -> Path = {
        Path(
            ComplexQualifier(
                TypeQualifier(this@activityScope::class),
                AndroidScopeKeys.ACTIVITY,
            ),
        )
    },
): Lazy<Scope> =
    lazy {
        val path =
            pathBuilder(
                ActivityScopePathBuilder(
                    Path(
                        ComplexQualifier(
                            TypeQualifier(this@activityScope::class),
                            AndroidScopeKeys.ACTIVITY,
                        ),
                    ),
                ),
            )
        var scope: Scope = getRootScope()
        for (qualifier in path.order) {
            scope = scope.getSubScope(qualifier)
        }
        scope.apply { initialize(this) }
    }

@JvmName("inlineActivityScope")
inline fun <reified T : Activity> ComponentActivity.activityScope(
    noinline pathBuilder: ActivityScopePathBuilder.() -> Path = {
        Path(
            ComplexQualifier(
                TypeQualifier(T::class),
                AndroidScopeKeys.ACTIVITY,
            ),
        )
    },
): Lazy<Scope> = activityScope(pathBuilder)

inline fun <reified T : ViewModel> ComponentActivity.viewModelScope(
    noinline pathBuilder: ViewModelScopePathBuilder.() -> Path = {
        Path(
            ComplexQualifier(
                TypeQualifier(T::class),
                AndroidScopeKeys.VIEWMODEL,
            ),
        )
    },
): Lazy<Scope> =
    lazy {
        val path =
            pathBuilder(
                ViewModelScopePathBuilder(
                    Path(
                        ComplexQualifier(
                            TypeQualifier(T::class),
                            AndroidScopeKeys.VIEWMODEL,
                        ),
                    ),
                ),
            )
        var scope: Scope = getRootScope()
        for (qualifier in path.order) {
            scope = scope.getSubScope(qualifier)
        }
        scope
    }

fun ComponentActivity.activityRetainedScope(
    pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = {
        Path(
            ComplexQualifier(
                TypeQualifier(this@activityRetainedScope::class),
                AndroidScopeKeys.ACTIVITY_RETAINED,
            ),
        )
    },
): Lazy<Scope> =
    lazy {
        val path =
            pathBuilder(
                ActivityRetainedScopePathBuilder(
                    Path(
                        ComplexQualifier(
                            TypeQualifier(this@activityRetainedScope::class),
                            AndroidScopeKeys.ACTIVITY_RETAINED,
                        ),
                    ),
                ),
            )
        var scope: Scope = getRootScope()
        for (qualifier in path.order) {
            scope = scope.getSubScope(qualifier)
        }
        scope.apply { registerActivityRetainedLifecycle(this) }
    }

@JvmName("inlineActivityRetainedScope")
inline fun <reified T : Activity> ComponentActivity.activityRetainedScope(
    noinline pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = {
        Path(
            ComplexQualifier(
                TypeQualifier(T::class),
                AndroidScopeKeys.ACTIVITY_RETAINED,
            ),
        )
    },
): Lazy<Scope> = activityRetainedScope(pathBuilder)

@MainThread
fun ComponentActivity.getRootScope() = (applicationContext as? DiComponent)?.rootScope ?: error("DiComponent의 하위 타입이 아닙니다. ")

@MainThread
internal fun ComponentActivity.registerActivityRetainedLifecycle(scope: Scope) {
    val viewModel = ViewModelProvider(this)[SavedHandleViewModel::class.java]
    if (viewModel.scope == null) {
        viewModel.scope = scope
        registerCurrentContext(scope)
        viewModel.addCloseable { scope.closeAll() }
    }
    viewModel.scope!!
}

@SuppressLint("RestrictedApi")
internal fun ComponentActivity.initialize(scope: Scope) {
    registerCurrentContext(scope)
    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                scope.closeAll()
                owner.lifecycle.removeObserver(this)
                super.onDestroy(owner)
            }
        },
    )
}

inline fun <reified T> ComponentActivity.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { getRootScope().get(qualifier) as T }
