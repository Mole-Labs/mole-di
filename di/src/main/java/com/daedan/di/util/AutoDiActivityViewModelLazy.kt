package com.daedan.di.util

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.daedan.di.path.AndroidScopes
import com.daedan.di.qualifier.TypeQualifier

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(
    scope: Lazy<AndroidScopes.ViewModelScope>,
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> =
    autoViewModels(
        TypeQualifier(VM::class),
        lazy { scope.value.scope },
        true,
        extrasProducer,
    )

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(noinline extrasProducer: (() -> CreationExtras)? = null): Lazy<VM> =
    autoViewModels(
        TypeQualifier(VM::class),
        lazy { getRootScope() },
        false,
        extrasProducer,
    )
