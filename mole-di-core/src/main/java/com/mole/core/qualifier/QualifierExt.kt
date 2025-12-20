package com.mole.core.qualifier

inline fun <reified T : Annotation> annotated(): AnnotationQualifier = AnnotationQualifier(T::class)

fun named(name: String): NamedQualifier = NamedQualifier(name)
