package com.mole.android

import com.mole.android.module.ModuleDefinition
import com.mole.android.module.combine
import com.mole.android.scope.Scope

interface DiComponent {
    val rootScope: Scope

    fun combineToRoot(vararg block: ModuleDefinition) = combine(rootScope, *block)
}
