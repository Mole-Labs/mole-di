package com.daedan.di.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.daedan.di.Scope
import com.daedan.di.dsl.path.AbstractPathBuilder
import com.daedan.di.path.Path
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@PublishedApi
internal inline fun <reified B : AbstractPathBuilder<B>> ComponentActivity.createScopeLazy(
    initialQualifier: Qualifier,
    noinline builderFactory: (Path) -> B,
    noinline pathBuilder: B.() -> Path,
    crossinline onResolved: (Scope) -> Unit = {},
): Lazy<Scope> =
    lazy {
        val path = builderFactory(Path(initialQualifier)).pathBuilder()
        getRootScope().resolvePath(path).also {
            onResolved(it)
        }
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
