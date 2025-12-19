package com.daedan.di.dsl.path

import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.path.Path
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

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
