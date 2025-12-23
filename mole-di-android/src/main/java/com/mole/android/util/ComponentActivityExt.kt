package com.mole.android.util

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mole.android.dsl.AndroidScopeKeys
import com.mole.android.dsl.path.ActivityRetainedScopePathBuilder
import com.mole.android.dsl.path.ActivityScopePathBuilder
import com.mole.android.dsl.path.ViewModelScopePathBuilder
import com.mole.android.scope.AndroidScopes
import com.mole.core.RootComponent
import com.mole.core.ScopeComponent
import com.mole.core.dsl.Root
import com.mole.core.path.Path
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

// --- Activity Scope ---
@MainThread
@Suppress("UNCHECKED_CAST")
fun ComponentActivity.activityScope(
    pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityScope> =
    lazy {
        this as? ScopeComponent<AndroidScopes.ActivityScope>
            ?: error("${this::class.java} is not a subtype of ScopeComponent. ")
        val scope =
            createScopeLazy<ActivityScopePathBuilder, AndroidScopes.ActivityScope>(
                initialQualifier =
                    ComplexQualifier(
                        TypeQualifier(this::class),
                        AndroidScopeKeys.ACTIVITY,
                    ),
                builderFactory = ::ActivityScopePathBuilder,
                pathBuilder = pathBuilder,
                onResolved = { initialize(it) },
            )
        if (!isInitialized()) {
            injectScope(scope)
        }
        scope.value
    }

@JvmName("inlineActivityScope")
@MainThread
inline fun <reified T : Activity> ComponentActivity.activityScope(
    noinline pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityScope> = activityScope(pathBuilder)

// --- ViewModel Scope ---
@MainThread
@Suppress("UNCHECKED_CAST")
@SuppressLint("RestrictedApi")
inline fun <reified T : ViewModel> ComponentActivity.viewModelScope(
    noinline pathBuilder: ViewModelScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ViewModelScope> =
    lazy {
        val scope =
            createScopeLazy<ViewModelScopePathBuilder, AndroidScopes.ViewModelScope>(
                initialQualifier =
                    ComplexQualifier(
                        TypeQualifier(T::class),
                        AndroidScopeKeys.VIEWMODEL,
                    ),
                builderFactory = ::ViewModelScopePathBuilder,
                pathBuilder = pathBuilder,
            )

        // If there is a scope linked to the viewModel, return the linked scope,
        // If the viewModel has not yet been created, return an independent scope instance
        val viewModel = viewModelStore[getViewModelKey(T::class.java)]
        if (viewModel != null) {
            viewModel as? ScopeComponent<AndroidScopes.ViewModelScope>
                ?: error("${T::class.java} is not a subtype of ScopeComponent. ")
            if (!viewModel.isInitialized()) {
                viewModel.injectScope(scope)
            }
            viewModel.scope.value
        } else {
            scope.value
        }
    }

// --- Activity Retained Scope ---
@MainThread
fun ComponentActivity.activityRetainedScope(
    pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityRetainedScope> =
    lazy {
        val viewModel = ViewModelProvider(this)[SavedHandleViewModel::class.java]
        if (viewModel.scope == null) {
            val scope =
                createScopeLazy<ActivityRetainedScopePathBuilder, AndroidScopes.ActivityRetainedScope>(
                    initialQualifier =
                        ComplexQualifier(
                            TypeQualifier(this::class),
                            AndroidScopeKeys.ACTIVITY_RETAINED,
                        ),
                    builderFactory = ::ActivityRetainedScopePathBuilder,
                    pathBuilder = pathBuilder,
                )
            viewModel.scope = scope.value
            viewModel.addCloseable { viewModel.scope!!.closeAll() }
            registerCurrentContext(viewModel.scope!!.scope, applicationContext)
        }
        viewModel.scope!!
    }

@JvmName("inlineActivityRetainedScope")
@MainThread
inline fun <reified T : Activity> ComponentActivity.activityRetainedScope(
    noinline pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityRetainedScope> = activityRetainedScope(pathBuilder)

@MainThread
fun ComponentActivity.getRootScope() =
    (applicationContext as? RootComponent)?.scope
        ?: error("Application is not a subtype of RootComponent.")

@MainThread
inline fun <reified T> ComponentActivity.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { getRootScope().get(qualifier) as T }
