package com.mole.core.dsl

import com.mole.core.path.Path

/**
 * A concrete implementation of [AbstractPathBuilder] for creating a scope resolution [Path].
 * This is the standard builder used for general-purpose path construction.
 *
 * @property path The [Path] object being constructed.
 */
class ScopePathBuilder(
    override val path: Path,
) : AbstractPathBuilder<ScopePathBuilder>()
