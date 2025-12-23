package com.mole.core.qualifier

/**
 * A DSL-style function to create an [AnnotationQualifier] for a given annotation type [T].
 * This provides a more concise and readable way to specify annotation-based qualifiers.
 *
 * @param T The annotation class to use as a qualifier.
 * @return An [AnnotationQualifier] instance for the specified annotation type.
 */
inline fun <reified T : Annotation> annotated(): AnnotationQualifier = AnnotationQualifier(T::class)

/**
 * A DSL-style function to create a [NamedQualifier] with the given [name].
 * This is a convenient way to create string-based qualifiers.
 *
 * @param name The string identifier for the dependency.
 * @return A [NamedQualifier] instance.
 */
fun named(name: String): NamedQualifier = NamedQualifier(name)
