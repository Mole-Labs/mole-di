package com.mole.core

import com.mole.core.scope.Scope

interface ScopeComponent<T : Scope> {
    val scope: Lazy<T>

    fun injectScope(lazyScope: Lazy<T>) = Unit

    fun isInitialized(): Boolean = true
}

class LazyBind<T : Scope> : ScopeComponent<T> {
    private var internalScope: Lazy<T>? = null

    override fun injectScope(lazyScope: Lazy<T>) {
        internalScope = lazyScope
    }

    override fun isInitialized() = internalScope != null

    override val scope: Lazy<T>
        get() = internalScope ?: error("Scope not initialized!")
}
