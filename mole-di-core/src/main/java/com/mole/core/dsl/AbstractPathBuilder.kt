package com.mole.core.dsl

import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier
import com.mole.core.qualifier.TypeQualifier

@ModuleBuilderDSL
abstract class AbstractPathBuilder<T : AbstractPathBuilder<T>> {
    abstract val path: Path

    @Suppress("UNCHECKED_CAST")
    val find: T get() = this as T

    inline fun <reified T : Any> scope(qualifier: Qualifier = TypeQualifier(T::class)): Qualifier = qualifier

    @Suppress("UNCHECKED_CAST")
    infix fun of(qualifier: Qualifier): T {
        path.append(qualifier)
        return this as T
    }

    infix fun of(root: Root): Path = path
}
