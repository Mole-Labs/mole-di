package com.daedan.di

import com.daedan.di.module.ModuleDefinition
import com.daedan.di.module.combine
import com.daedan.di.scope.Scope

interface DiComponent {
    val rootScope: Scope

    fun combineToRoot(vararg block: ModuleDefinition) = combine(rootScope, *block)
}
