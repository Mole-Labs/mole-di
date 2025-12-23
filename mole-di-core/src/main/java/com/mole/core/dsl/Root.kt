package com.mole.core.dsl

/**
 * A singleton object used as a terminal symbol in the scope path DSL.
 * It represents the top-level, or root, of the scope hierarchy, making the path definitions more explicit and readable.
 *
 * Example:
 * `find of scope<MyScope>() of Root`
 */
data object Root
