@file:Suppress("ktlint:standard:filename")

package com.mole.android.fixture

import androidx.lifecycle.ViewModel
import com.mole.android.scope.AndroidScopes
import com.mole.core.scope.LazyBind
import com.mole.core.scope.ScopeComponent

class TestViewModel(
    val arg1: Child1,
) : ViewModel(),
    ScopeComponent<AndroidScopes.ViewModelScope> by LazyBind()
