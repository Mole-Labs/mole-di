package com.mole.android.qualifier

inline fun <reified T : Annotation> annotated(): AnnotationQualifier = AnnotationQualifier(T::class)

fun named(name: String): NamedQualifier = NamedQualifier(name)
