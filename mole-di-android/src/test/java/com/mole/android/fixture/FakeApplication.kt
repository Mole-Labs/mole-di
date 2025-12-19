package com.mole.android.fixture

import android.app.Application
import com.mole.android.DiComponent
import com.mole.android.scope.Scope

class FakeApplication :
    Application(),
    DiComponent {
    override val rootScope = Scope()
}
