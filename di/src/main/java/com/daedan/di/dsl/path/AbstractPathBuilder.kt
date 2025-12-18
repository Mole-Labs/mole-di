package com.daedan.di.dsl.path

import com.daedan.di.dsl.ModuleBuilderDSL
import com.daedan.di.path.Path
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier

@ModuleBuilderDSL
abstract class AbstractPathBuilder {
    @PublishedApi
    internal abstract val path: Path

    val find: AbstractPathBuilder get() = this

    inline fun <reified T : Any> scope(qualifier: Qualifier = TypeQualifier(T::class)): Qualifier = qualifier

    infix fun of(qualifier: Qualifier): AbstractPathBuilder {
        path.append(qualifier)
        return this
    }

    infix fun of(root: Root): Path = path
}
