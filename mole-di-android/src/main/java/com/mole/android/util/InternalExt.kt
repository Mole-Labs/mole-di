package com.mole.android.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mole.android.scope.AndroidScopes
import com.mole.core.dsl.AbstractPathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.Scope

@PublishedApi
@Suppress("UNCHECKED_CAST")
internal inline fun <reified B : AbstractPathBuilder<B>, reified SCOPE : AndroidScopes> ComponentActivity.createScopeLazy(
    initialQualifier: Qualifier,
    noinline builderFactory: (Path) -> B,
    noinline pathBuilder: B.() -> Path,
    crossinline onResolved: (Scope) -> Unit = {},
): Lazy<SCOPE> =
    lazy {
        val path = builderFactory(Path(initialQualifier)).pathBuilder()
        val scope =
            getRootScope().resolvePath(path).also {
                onResolved(it)
            }
        when (SCOPE::class) {
            AndroidScopes.ActivityScope::class -> AndroidScopes.ActivityScope(scope)
            AndroidScopes.ViewModelScope::class -> AndroidScopes.ViewModelScope(scope)
            AndroidScopes.ActivityRetainedScope::class -> AndroidScopes.ActivityRetainedScope(scope)
            else -> error("올바르지 않은 Android Scope 입니다.")
        } as SCOPE
    }

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

internal fun Context.registerCurrentContext(scope: Scope) {
    scope.declare(
        qualifier = TypeQualifier(Context::class),
        instance = this,
    )
}

@MainThread
@PublishedApi
internal inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(
    qualifier: Qualifier = TypeQualifier(VM::class),
    scope: Lazy<Scope>,
    addCloseFlag: Boolean,
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> {
    val factory =
        viewModelFactory {
            initializer {
                val viewModel = scope.value.get(qualifier) as VM
                if (addCloseFlag) {
                    viewModel.addCloseable { scope.value.closeAll() }
                }
                viewModel
            }
        }
    return ViewModelLazy(
        VM::class,
        { viewModelStore },
        { factory },
        { extrasProducer?.invoke() ?: this.defaultViewModelCreationExtras },
    )
}
