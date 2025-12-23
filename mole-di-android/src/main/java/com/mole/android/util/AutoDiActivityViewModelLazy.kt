package com.mole.android.util

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import com.mole.android.scope.AndroidScopes
import com.mole.core.qualifier.TypeQualifier

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(
    scope: Lazy<AndroidScopes.ViewModelScope>,
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> =
    autoViewModels(
        TypeQualifier(VM::class),
        scope,
        extrasProducer,
    )

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.autoViewModels(noinline extrasProducer: (() -> CreationExtras)? = null): Lazy<VM> =
    autoViewModels(
        TypeQualifier(VM::class),
        lazy { getRootScope() },
        extrasProducer,
    )
