package com.mole.android.scope

import com.mole.core.scope.Scope
import com.mole.core.scope.ScopeImpl

sealed interface AndroidScopes : Scope {
    val scope: ScopeImpl

    @JvmInline
    value class ActivityScope(
        override val scope: ScopeImpl,
    ) : Scope by scope,
        AndroidScopes

    @JvmInline
    value class ViewModelScope
        @PublishedApi
        internal constructor(
            override val scope: ScopeImpl,
        ) : Scope by scope,
            AndroidScopes

    @JvmInline
    value class ActivityRetainedScope
        @PublishedApi
        internal constructor(
            override val scope: ScopeImpl,
        ) : Scope by scope,
            AndroidScopes
}
