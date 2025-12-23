package com.mole.core.scope

import com.mole.core.module.DependencyModule
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier

interface Scope {
    /** Resolve Instance */
    fun get(qualifier: Qualifier): Any

    /** Get sub scope */
    fun getSubScope(qualifier: Qualifier): Scope

    /** Search for sub-scopes via path */
    fun resolvePath(path: Path): Scope

    fun declare(
        qualifier: Qualifier,
        instance: Any,
    )

    /** Register dependency module */
    fun registerFactory(vararg modules: DependencyModule)

    /** Close scope and release resources */
    fun closeAll()
}
