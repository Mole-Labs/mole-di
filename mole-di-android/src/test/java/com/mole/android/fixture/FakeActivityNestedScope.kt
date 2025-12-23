package com.mole.android.fixture

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mole.android.scope.AndroidScopes
import com.mole.android.scope.inject
import com.mole.android.util.activityRetainedScope
import com.mole.android.util.activityScope
import com.mole.core.ScopeComponent
import com.mole.core.dsl.Root

class FakeActivityNestedScope :
    ComponentActivity(),
    ScopeComponent<AndroidScopes.ActivityScope> {
    override val scope =
        activityScope {
            find of activityRetainedScope<FakeActivityNestedScope>() of Root
        }

    val activityRetainedScope = activityRetainedScope()

    val activityArgument by scope.inject<Child2>()

    val activityRetainedArgument by activityRetainedScope.inject<Child3>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityArgument
        activityRetainedArgument
    }
}
