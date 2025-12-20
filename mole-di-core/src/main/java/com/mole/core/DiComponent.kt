package com.mole.core

import com.mole.core.module.ModuleDefinition
import com.mole.core.module.combine
import com.mole.core.scope.Scope

interface DiComponent {
    val rootScope: Scope

    fun combineToRoot(vararg block: ModuleDefinition) = combine(rootScope, *block)
}
