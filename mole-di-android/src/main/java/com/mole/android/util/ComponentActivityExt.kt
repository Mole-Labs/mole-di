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
import com.mole.core.dsl.Root
import com.mole.core.path.Path
import com.mole.core.qualifier.ComplexQualifier
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.RootComponent
import com.mole.core.scope.ScopeComponent

/**
 * Lazily creates or retrieves an [AndroidScopes.ActivityScope] tied to this [ComponentActivity].
 * The scope is automatically created and attached to the activity's lifecycle.
 * It will be closed when the activity is destroyed.
 *
 * @param pathBuilder A DSL block to define the path to the parent scope. Defaults to the root scope.
 * @return A [Lazy] delegate that provides the [AndroidScopes.ActivityScope].
 */
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

/**
 * A type-safe overload of [activityScope] for inline usage.
 */
@JvmName("inlineActivityScope")
@MainThread
inline fun <reified T : Activity> ComponentActivity.activityScope(
    noinline pathBuilder: ActivityScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityScope> = activityScope(pathBuilder)

/**
 * Lazily creates or retrieves a [AndroidScopes.ViewModelScope] for a specific [ViewModel] type [T].
 * This scope is tied to the lifecycle of the ViewModel, surviving configuration changes.
 * The scope is automatically closed when the ViewModel is cleared.
 *
 * If the ViewModel is not yet attached to an Activity, it creates and returns a new
 * ViewModelScope. Otherwise, it returns the existing scope attached to the
 * ViewModel retrieved from the ViewModelProvider.
 *
 * @param T The type of the [ViewModel] this scope is for.
 * @param pathBuilder A DSL block to define the path to the parent scope. Defaults to the root scope.
 * @return A [Lazy] delegate that provides the [AndroidScopes.ViewModelScope].
 */
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

/**
 * Lazily creates or retrieves an [AndroidScopes.ActivityRetainedScope] tied to this [ComponentActivity].
 * This scope survives activity recreation due to configuration changes (e.g., screen rotation).
 * It is managed by a hidden [SavedHandleViewModel] and is closed only when the activity is finished for good.
 *
 * @param pathBuilder A DSL block to define the path to the parent scope. Defaults to the root scope.
 * @return A [Lazy] delegate that provides the [AndroidScopes.ActivityRetainedScope].
 */
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

/**
 * A type-safe overload of [activityRetainedScope] for inline usage.
 */
@JvmName("inlineActivityRetainedScope")
@MainThread
inline fun <reified T : Activity> ComponentActivity.activityRetainedScope(
    noinline pathBuilder: ActivityRetainedScopePathBuilder.() -> Path = { find of Root },
): Lazy<AndroidScopes.ActivityRetainedScope> = activityRetainedScope(pathBuilder)

/**
 * Retrieves the root scope from the Application context.
 * The Application class must implement the [RootComponent] interface.
 *
 * @return The root [com.mole.core.scope.Scope] of the application.
 * @throws IllegalStateException if the Application does not implement [RootComponent].
 */
@MainThread
fun ComponentActivity.getRootScope() =
    (applicationContext as? RootComponent)?.scope
        ?: error("Application is not a subtype of RootComponent.")

/**
 * A helper function to lazily inject a dependency from the root scope.
 *
 * @param T The type of the dependency to inject.
 * @param qualifier An optional [Qualifier] if you need to distinguish between dependencies of the same type.
 * @param mode The [LazyThreadSafetyMode] for the lazy delegate.
 * @return A [Lazy] delegate that provides the dependency instance [T].
 */
@MainThread
inline fun <reified T> ComponentActivity.inject(
    qualifier: Qualifier = TypeQualifier(T::class),
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
): Lazy<T> = lazy(mode) { getRootScope().get(qualifier) as T }
