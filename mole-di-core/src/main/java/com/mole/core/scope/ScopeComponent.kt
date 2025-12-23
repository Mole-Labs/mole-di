package com.mole.core.scope

/**
 * An interface for components that are associated with a [Scope].
 * This is typically implemented by Android components like `Activity` or `ViewModel`
 * to enable scope lifecycle management and dependency injection.
 *
 * @param T The type of the [Scope] this component is associated with.
 */
interface ScopeComponent<T : Scope> {
    /**
     * A lazy-initialized property that holds the [Scope] instance.
     * Accessing this property before the scope is injected will result in an error.
     */
    val scope: Lazy<T>

    /**
     * Injects the actual [Scope] instance into the component.
     * This method is designed to be called during the component's initialization phase (e.g., `Activity.onCreate`).
     *
     * @param lazyScope A lazy provider for the scope to be injected.
     */
    fun injectScope(lazyScope: Lazy<T>) = Unit

    /**
     * Checks if the scope has been injected and initialized.
     *
     * @return `true` if the scope is ready, `false` otherwise.
     */
    fun isInitialized(): Boolean = true
}

/**
 * A delegate class that implements [ScopeComponent] to provide a late-initialized scope.
 * It acts as a placeholder for a [Scope] that will be injected later in the component's lifecycle.
 * This is particularly useful in Android where context-dependent scopes cannot be created at property initialization time.
 *
 * Usage (for Android):
 * ```
 * class MyActivity : AppCompatActivity(), ScopeComponent<MyScope> by LazyBind() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         injectScope(lazy { createMyScope() })
 *         // Now you can access scope.value
 *     }
 * }
 * ```
 * @param T The type of the [Scope] being delegated.
 */
class LazyBind<T : Scope> : ScopeComponent<T> {
    private var internalScope: Lazy<T>? = null

    override fun injectScope(lazyScope: Lazy<T>) {
        internalScope = lazyScope
    }

    override fun isInitialized() = internalScope != null

    override val scope: Lazy<T>
        get() =
            internalScope
                ?: error("Scope has not been initialized. Did you forget to call injectScope()?")
}
