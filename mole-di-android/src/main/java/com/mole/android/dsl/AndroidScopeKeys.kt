package com.mole.android.dsl

/**
 * An enum class that provides standard keys for identifying common Android-specific scopes.
 * These keys are used to create unique identifiers for activity, view model, and retained scopes.
 */
enum class AndroidScopeKeys {
    /**
     * Identifier for a scope tied to a ViewModel's lifecycle.
     */
    VIEWMODEL,

    /**
     * Identifier for a scope tied to an Activity's lifecycle.
     */
    ACTIVITY,

    /**
     * Identifier for a scope that is retained across Activity recreation (e.g., on configuration changes).
     */
    ACTIVITY_RETAINED,
}
