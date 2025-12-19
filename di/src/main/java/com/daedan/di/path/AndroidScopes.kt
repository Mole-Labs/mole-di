package com.daedan.di.path

import com.daedan.di.scope.Scope

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
