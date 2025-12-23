package com.mole.android.fixture

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mole.android.scope.AndroidScopes
import com.mole.android.scope.inject
import com.mole.android.util.activityScope
import com.mole.core.LazyBind
import com.mole.core.ScopeComponent

class FakeActivityLazyBind :
    ComponentActivity(),
    ScopeComponent<AndroidScopes.ActivityScope> by LazyBind() {
    val activityArgument by activityScope().inject<Child2>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityArgument
    }
}
