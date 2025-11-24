package com.daedan.di.util

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.daedan.di.Scope
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

inline fun <reified T> Scope.inject(qualifier: Qualifier = TypeQualifier(T::class)): Lazy<T> =
    lazy {
        get(qualifier) as T
    }

inline fun <reified T> ComponentActivity.inject(qualifier: Qualifier = TypeQualifier(T::class)): Lazy<T> =
    lazy {
        getRootScope().get(qualifier) as T
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
