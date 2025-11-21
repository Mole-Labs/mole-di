package com.daedan.di.util

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import com.daedan.di.DiComponent
import com.daedan.di.Scope
import com.daedan.di.qualifier.TypeQualifier

@MainThread
fun ComponentActivity.registerActivityRetainedLifecycle(scope: Scope) {
    val viewModel = ViewModelProvider(this)[SavedHandleViewModel::class.java]
    if (viewModel.scope == null) {
        viewModel.scope = scope
        registerCurrentContext(scope)
        viewModel.addCloseable { scope.closeAll() }
    }
    viewModel.scope!!
}

@MainThread
@JvmName("registerActivityRetainedLifecycleGeneric")
inline fun <reified T : Any> ComponentActivity.registerActivityRetainedLifecycle() {
    val scope = getRootScope().getSubScope<T>()
    registerActivityRetainedLifecycle(scope)
}

@MainThread
fun ComponentActivity.registerActivityRetainedLifecycle() {
    val scope = getRootScope().getSubScope(TypeQualifier(this::class))
    registerActivityRetainedLifecycle(scope)
}

@MainThread
fun ComponentActivity.getRootScope() = (applicationContext as? DiComponent)?.rootScope ?: error("DiComponent의 하위 타입이 아닙니다. ")
