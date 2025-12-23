package com.mole.android.fixture

import android.app.Application
import com.mole.core.RootComponent
import com.mole.core.qualifier.RootScopeQualifier
import com.mole.core.scope.ScopeImpl

class FakeApplication :
    Application(),
    RootComponent {
    override val scope = ScopeImpl(RootScopeQualifier)
}
