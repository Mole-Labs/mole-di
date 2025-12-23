package com.mole.android.fixture

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mole.android.scope.inject
import com.mole.android.util.autoViewModels
import com.mole.android.util.viewModelScope

class FakeViewModelScopeActivity : ComponentActivity() {
    val viewModelScope = viewModelScope<TestViewModel>()
    val viewModel by autoViewModels<TestViewModel>(viewModelScope)

    val arg1 by viewModelScope.inject<Child1>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
    }
}
