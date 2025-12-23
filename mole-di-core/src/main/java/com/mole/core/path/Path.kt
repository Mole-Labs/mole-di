package com.mole.core.path

import com.mole.core.qualifier.Qualifier

/**
 * Represents a hierarchical path to a nested scope within the DI container.
 * A path is essentially a sequence of [Qualifier]s that defines the route from a starting scope to a target descendant scope.
 * The path is resolved in reverse order of appendage, moving from the target scope up to the root.
 */
class Path {
    private val _order: MutableList<Qualifier>

    /**
     * Creates an empty path.
     */
    constructor() {
        _order = arrayListOf()
    }

    /**
     * Creates a path with an initial qualifier.
     * @param initialValue The first [Qualifier] in the path, typically representing the starting scope.
     */
    constructor(initialValue: Qualifier) {
        _order = arrayListOf(initialValue)
    }

    /**
     * A read-only list of the qualifiers that make up the path, in the order they should be resolved.
     */
    val order: List<Qualifier> get() = _order.toList()

    /**
     * Prepends a [Qualifier] to the path.
     * Since the path is built from the target scope upwards, `append` actually adds the qualifier to the front of the resolution order.
     *
     * @param qualifier The [Qualifier] to prepend to the path.
     */
    fun append(qualifier: Qualifier) {
        _order.add(0, qualifier)
    }
}
