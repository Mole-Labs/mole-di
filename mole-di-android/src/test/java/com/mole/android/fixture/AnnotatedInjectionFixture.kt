package com.mole.android.fixture

annotation class TestComponent1

annotation class TestComponent2

interface ComponentObject

@TestComponent1
class ComponentObject1 : ComponentObject

@TestComponent2
class ComponentObject2 : ComponentObject

annotation class GeneralAnnotation

@GeneralAnnotation
class GeneralObject
