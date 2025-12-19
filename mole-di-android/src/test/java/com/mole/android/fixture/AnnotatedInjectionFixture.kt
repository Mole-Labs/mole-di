package com.mole.android.fixture

import com.mole.android.annotation.Component
import com.mole.android.annotation.Inject

@Component
annotation class TestComponent1

@Component
annotation class TestComponent2

interface ComponentObject

@TestComponent1
class ComponentObject1 : ComponentObject

@TestComponent2
class ComponentObject2 : ComponentObject

annotation class GeneralAnnotation

@GeneralAnnotation
class GeneralObject

class FieldInjection {
    @Inject
    private lateinit var field1: Child1

    @Inject
    private lateinit var field2: Child2

    fun assertPropertyInitialized() {
        assert(::field1.isInitialized)
        assert(::field2.isInitialized)
    }
}

class FieldInjectionWithName {
    @Inject(name = "parent1")
    private lateinit var field1: Parent

    @Inject(name = "parent2")
    private lateinit var field2: Parent

    fun assertPropertyInitialized() {
        assert(::field1.isInitialized)
        assert(::field2.isInitialized)
    }
}

class FieldInjectionWithAnnotation {
    @TestComponent1
    private lateinit var field1: ComponentObject

    @TestComponent2
    private lateinit var field2: ComponentObject

    fun assertPropertyInitialized() {
        assert(::field1.isInitialized)
        assert(::field2.isInitialized)
    }
}

class ConstructorInjectionWithName(
    @Inject(name = "parent1")
    private val constructor1: Parent,
    @Inject(name = "parent2")
    private val constructor2: Parent,
)

class ConstructorInjectionWithAnnotation(
    @TestComponent1
    private val constructor1: ComponentObject,
    @TestComponent2
    private val constructor2: ComponentObject,
)

class FieldAndConstructorInjection(
    @Inject(name = "parent1")
    private val field1: Parent,
    @TestComponent1
    private val field2: ComponentObject,
) {
    @Inject(name = "parent2")
    private lateinit var constructor1: Parent

    @TestComponent2
    private lateinit var constructor2: ComponentObject
}
