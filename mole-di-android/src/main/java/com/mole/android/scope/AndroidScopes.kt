package com.mole.android.scope

import com.mole.core.scope.DefaultScope
import com.mole.core.scope.Scope

/**
 * A sealed interface representing the different types of scopes available in the Android extension module.
 * This provides a way to distinguish between scopes with different lifecycles, such as Activity, ViewModel, and retained scopes.
 * It uses the delegation pattern to wrap a core [DefaultScope] instance.
 */
sealed interface AndroidScopes : Scope {
    /**
     * The underlying core [DefaultScope] instance.
     */
    val scope: DefaultScope

    /**
     * A scope that is tied to the lifecycle of an Activity.
     * It is created when the Activity is created and destroyed when the Activity is destroyed.
     * Uses [JvmInline] for performance optimization by avoiding a wrapper class allocation at runtime.
     */
    @JvmInline
    value class ActivityScope(
        override val scope: DefaultScope,
    ) : Scope by scope,
        AndroidScopes

    /**
     * A scope that is tied to the lifecycle of a ViewModel.
     * This scope survives configuration changes and is only destroyed when the ViewModel is cleared.
     * Uses [JvmInline] for performance optimization.
     */
    @JvmInline
    value class ViewModelScope
        @PublishedApi
        internal constructor(
            override val scope: DefaultScope,
        ) : Scope by scope,
            AndroidScopes

    /**
     * A scope that is retained across Activity recreation (e.g., on screen rotation).
     * It is managed by a hidden ViewModel, making it survive configuration changes but not process death.
     * Uses [JvmInline] for performance optimization.
     */
    @JvmInline
    value class ActivityRetainedScope
        @PublishedApi
        internal constructor(
            override val scope: DefaultScope,
        ) : Scope by scope,
            AndroidScopes
}
