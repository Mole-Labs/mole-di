class Parent(
    private val child1: Child1,
    private val child2: Child2,
)

class SameDependencyParent(
    private val child1: Child1,
    private val child2: Child1,
)

class Child1

class Child2

class NestedDependency(
    private val parent: Parent,
)

class CircularDependency1(
    private val circularDependency2: CircularDependency2,
)

class CircularDependency2(
    private val circularDependency1: CircularDependency1,
)

annotation class TestComponent1

annotation class TestComponent2

interface ComponentObject
