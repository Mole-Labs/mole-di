package com.daedan.di.util

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.daedan.di.Scope
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(
    qualifier: Qualifier = TypeQualifier(VM::class),
    scope: Lazy<Scope> = lazy { getRootScope() },
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> {
    val factory =
        viewModelFactory {
            initializer {
                val viewModel = scope.value.get(qualifier) as VM
                viewModel.addCloseable { scope.value.closeAll() }
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

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(
    scope: Lazy<Scope> = lazy { getRootScope() },
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> = autoViewModels(TypeQualifier(VM::class), scope, extrasProducer)
