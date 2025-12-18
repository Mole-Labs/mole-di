package com.daedan.di.util

import android.app.Activity
import androidx.activity.ComponentActivity
import com.daedan.di.Scope
import com.daedan.di.dsl.path.ActivityScopePathBuilder
import com.daedan.di.path.Path
import com.daedan.di.qualifier.TypeQualifier

fun ComponentActivity.activityScope(
    pathBuilder: ActivityScopePathBuilder.() -> Path = {
        Path(TypeQualifier(this::class))
    },
): Scope {
    val path = pathBuilder(ActivityScopePathBuilder(Path()))
    var scope: Scope = getRootScope()
    for (qualifier in path.order) {
        scope = scope.getSubScope(qualifier)
    }
    return scope
}

inline fun <reified T : Activity> ComponentActivity.activityScope(
    noinline pathBuilder: ActivityScopePathBuilder.() -> Path = {
        Path(TypeQualifier(T::class))
    },
): Scope = activityScope(pathBuilder)
