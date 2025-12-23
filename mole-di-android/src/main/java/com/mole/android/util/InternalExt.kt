package com.mole.android.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mole.android.scope.AndroidScopes
import com.mole.core.ScopeComponent
import com.mole.core.dsl.AbstractPathBuilder
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.Scope
import com.mole.core.scope.ScopeImpl

@PublishedApi
@Suppress("UNCHECKED_CAST")
internal inline fun <reified B : AbstractPathBuilder<B>, reified SCOPE : AndroidScopes> ComponentActivity.createScopeLazy(
    initialQualifier: Qualifier,
    noinline builderFactory: (Path) -> B,
    noinline pathBuilder: B.() -> Path,
    crossinline onResolved: (ScopeImpl) -> Unit = {},
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
            else -> error("Invalid Android Scope.")
        } as SCOPE
    }

@SuppressLint("RestrictedApi")
internal fun ComponentActivity.initialize(scope: ScopeImpl) {
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

internal fun Context.registerCurrentContext(
    scope: ScopeImpl,
    context: Context = this,
) {
    scope.declare(
        qualifier = TypeQualifier(Context::class),
        instance = context,
    )
}

@MainThread
@PublishedApi
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(
    qualifier: Qualifier = TypeQualifier(VM::class),
    scope: Lazy<Scope>,
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> {
    val factory =
        viewModelFactory {
            initializer {
                val scope = scope.value
                val viewModel = scope.get(qualifier) as VM
                if (scope is AndroidScopes.ViewModelScope) {
                    (viewModel as? ScopeComponent<AndroidScopes.ViewModelScope>)?.injectScope(lazy { scope })
                        ?: error("${VM::class.java} is not a subtype of ScopeComponent.")
                    viewModel.addCloseable { scope.closeAll() }
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

@PublishedApi
internal fun getViewModelKey(clazz: Class<out ViewModel>): String {
    val canonicalName =
        clazz.canonicalName
            ?: throw IllegalArgumentException("Local and anonymous classes can't be ViewModels")
    return "androidx.lifecycle.ViewModelProvider.DefaultKey:$canonicalName"
}
