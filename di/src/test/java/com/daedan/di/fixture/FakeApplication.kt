package com.daedan.di.fixture

import android.app.Application
import com.daedan.di.DiComponent
import com.daedan.di.Scope

class FakeApplication :
    Application(),
    DiComponent {
    override val rootScope = Scope()
}
