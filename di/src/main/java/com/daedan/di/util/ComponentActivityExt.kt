package com.daedan.di.util

import androidx.activity.ComponentActivity
import com.daedan.di.Scope
import com.daedan.di.dsl.finder.AbstractFinderBuilder
import com.daedan.di.dsl.finder.ScopeFinderBuilder
import com.daedan.di.finder.Finder
import com.daedan.di.qualifier.TypeQualifier

fun ComponentActivity.activityScope(
    finderScope: AbstractFinderBuilder.() -> Finder = {
        Finder(TypeQualifier(this::class))
    },
): Scope =
    activityScope(
        finder = finderScope(ScopeFinderBuilder()),
        scope = getRootScope(),
    )

inline fun <reified T : Any> ComponentActivity.activityScope(
    finderScope: AbstractFinderBuilder.() -> Finder = {
        Finder(TypeQualifier(T::class))
    },
): Scope =
    activityScope(
        finder = finderScope(ScopeFinderBuilder()),
        scope = getRootScope(),
    )

@PublishedApi
internal fun ComponentActivity.activityScope(
    finder: Finder,
    scope: Scope = getRootScope(),
): Scope =
    if (finder.next != null) {
        val qualifier = finder.qualifier
        val scope = scope.getSubScope(qualifier)
        activityScope(finder.next, scope)
    } else {
        scope
    }
