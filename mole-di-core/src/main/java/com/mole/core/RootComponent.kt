package com.mole.core

import com.mole.core.module.ModuleDefinition
import com.mole.core.module.combine
import com.mole.core.scope.ScopeImpl

interface RootComponent {
    val scope: ScopeImpl

    fun combineToRoot(vararg block: ModuleDefinition) = combine(scope, *block)
}
