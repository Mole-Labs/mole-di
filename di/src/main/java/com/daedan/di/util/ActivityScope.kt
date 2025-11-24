package com.daedan.di.util

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.daedan.di.Scope
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@MainThread
fun ComponentActivity.registerActivityScope(scope: Scope) {
    initialize(scope)
}

@MainThread
fun ComponentActivity.registerActivityScope() {
    val scope = getRootScope().getSubScope(TypeQualifier(this::class))
    initialize(scope)
}

@MainThread
@JvmName("registerActivityScopeWithGeneric")
inline fun <reified T : Any> ComponentActivity.registerActivityScope() {
    val scope = getRootScope().getSubScope<T>()
    initialize(scope)
}

inline fun <reified T> ComponentActivity.inject(
    scope: Lazy<Scope> = lazy { getRootScope() },
    qualifier: Qualifier = TypeQualifier(T::class),
): Lazy<T> =
    lazy {
        scope.value.get(qualifier) as T
    }

@SuppressLint("RestrictedApi")
fun ComponentActivity.initialize(scope: Scope) {
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
