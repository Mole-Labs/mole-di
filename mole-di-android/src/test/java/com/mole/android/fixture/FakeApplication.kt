package com.mole.android.fixture

import android.app.Application

class FakeApplication :
    Application(),
    DiComponent {
    override val rootScope = Scope()
}
