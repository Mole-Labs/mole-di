package com.mole.android.fixture

class Parent(
    private val child1: Child1,
    private val child2: Child2,
)

class Child1

class Child2

class Child3

class NestedDependency(
    private val parent: Parent,
)

class CircularDependency1(
    private val circularDependency2: CircularDependency2,
)

class CircularDependency2(
    private val circularDependency1: CircularDependency1,
)

interface NoConstructorObject

class UnableReflectObject(
    private val noConstructorObject: NoConstructorObject,
)
