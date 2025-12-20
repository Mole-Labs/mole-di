package com.mole.android.scope

import com.mole.core.scope.Scope

sealed interface AndroidScopes {
    val scope: Scope

    @JvmInline
    value class ActivityScope
        @PublishedApi
        internal constructor(
            override val scope: Scope,
        ) : AndroidScopes

    @JvmInline
    value class ViewModelScope
        @PublishedApi
        internal constructor(
            override val scope: Scope,
        ) : AndroidScopes

    @JvmInline
    value class ActivityRetainedScope
        @PublishedApi
        internal constructor(
            override val scope: Scope,
        ) : AndroidScopes
}
