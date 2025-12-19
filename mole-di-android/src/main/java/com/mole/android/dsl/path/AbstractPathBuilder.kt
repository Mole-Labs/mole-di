package com.mole.android.dsl.path

import com.mole.android.dsl.ModuleBuilderDSL
import com.mole.android.path.Path
import com.mole.android.qualifier.Qualifier
import com.mole.android.qualifier.TypeQualifier

@ModuleBuilderDSL
abstract class AbstractPathBuilder<T : AbstractPathBuilder<T>> {
    @PublishedApi
    internal abstract val path: Path

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
