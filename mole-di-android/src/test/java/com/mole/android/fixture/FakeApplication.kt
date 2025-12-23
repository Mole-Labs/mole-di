package com.mole.android.fixture

import android.app.Application
import com.mole.core.qualifier.RootScopeQualifier
import com.mole.core.scope.DefaultScope
import com.mole.core.scope.RootComponent

class FakeApplication :
    Application(),
    RootComponent {
    override val scope = DefaultScope(RootScopeQualifier)
}
