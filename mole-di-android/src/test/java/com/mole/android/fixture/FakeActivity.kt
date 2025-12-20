package com.mole.android.fixture

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mole.android.scope.inject
import com.mole.android.util.activityRetainedScope
import com.mole.android.util.autoViewModels
import com.mole.android.util.registerActivityScope

class FakeActivity : ComponentActivity() {
    val viewModel by autoViewModels<TestViewModel>()

    val activityScope = registerActivityScope()

    val activityRetainedScope = activityRetainedScope()

    val activityArgument by inject<Child2>(activityScope)

    val activityRetainedArgument by inject<Child3>(activityRetainedScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        activityArgument
        activityRetainedArgument
    }
}

class FakeInvalidScopeActivity : ComponentActivity() {
    val activityScope = registerActivityScope()

    val activityArgument by inject<Parent>(activityScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        activityArgument
        super.onCreate(savedInstanceState)
    }
}

fun DiComponent.testModule() =
    root {
        scope<TestViewModel> {
            scoped { Child1() }
        }
        scope<FakeActivity> {
            scoped { Child2() }
            scoped { Child3() }
        }
        viewModel {
            TestViewModel(get(scope = it))
        }
    }

fun DiComponent.invalidScopeModule() =
    root {
        single { Child2() }
        scope<FakeInvalidScopeActivity> {
            scoped { Child1() }
        }
        single { Parent(get(), get()) }
    }
