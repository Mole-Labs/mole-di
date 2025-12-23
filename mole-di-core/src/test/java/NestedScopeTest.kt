import com.mole.core.module.combine
import com.mole.core.path.Path
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.ScopeImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class NestedScopeTest {
    @Test
    fun `can get an instance of a scope registered through scope`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            scope<TestComponent1> {
                single { Child1() }
                factory { Child2() }
                single { Parent(child1 = get(), child2 = get()) }
                single { NestedDependency(get()) }
            }
        }

        // when
        val actual =
            scope.getSubScope(TypeQualifier(TestComponent1::class)).get(
                TypeQualifier(NestedDependency::class),
            )

        // then
        assertThat(actual).isInstanceOf(NestedDependency::class.java)
    }

    @Test
    fun `can apply nested scopes`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            scope<TestComponent1> {
                scope<TestComponent2> {
                    scope<Child1> {
                        single { Child1() }
                        factory { Child2() }
                        single { Parent(child1 = get(), child2 = get()) }
                        single { NestedDependency(get()) }
                    }
                }
            }
        }

        // when
        val actual =
            scope
                .getSubScope(TypeQualifier(TestComponent1::class))
                .getSubScope(TypeQualifier(TestComponent2::class))
                .getSubScope(TypeQualifier(Child1::class))
                .get(
                    TypeQualifier(NestedDependency::class),
                )

        // then
        assertThat(actual).isInstanceOf(NestedDependency::class.java)
    }

    @Test
    fun `cannot find a dependency registered in another scope from one scope`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            scope<TestComponent1> {
                scope<TestComponent2> {
                    single { Child1() }
                    single { NestedDependency(get()) }
                    single { Parent(child1 = get(), child2 = get()) }
                }

                scope<Child1> {
                    factory { Child2() }
                }
            }
        }

        // when - then
        assertThatThrownBy {
            scope
                .getSubScope(TypeQualifier(TestComponent1::class))
                .getSubScope(TypeQualifier(TestComponent2::class))
                .get(
                    TypeQualifier(NestedDependency::class),
                )
        }.message().contains("Cannot find instance in container")
    }

    @Test
    fun `can find a dependency registered in a parent scope from one scope`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            single { Child2() }
            scope<TestComponent1> {
                factory { Child1() }
                scope<TestComponent2> {
                    single { NestedDependency(get()) }
                    single { Parent(child1 = get(), child2 = get()) }
                }
            }
        }

        // when
        val actual =
            scope
                .getSubScope(TypeQualifier(TestComponent1::class))
                .getSubScope(TypeQualifier(TestComponent2::class))
                .get(
                    TypeQualifier(NestedDependency::class),
                )

        // then
        assertThat(actual).isInstanceOf(NestedDependency::class.java)
    }

    @Test
    fun `can find a scope through Path`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            single { Child2() }
            scope<TestComponent1> {
                factory { Child1() }
                scope<TestComponent2> {
                    single { NestedDependency(get()) }
                    single { Parent(child1 = get(), child2 = get()) }
                }
            }
        }

        // when
        val actual =
            scope.resolvePath(
                Path().apply {
                    append(TypeQualifier(TestComponent2::class))
                    append(TypeQualifier(TestComponent1::class))
                },
            )

        // then
        assertThat(actual.qualifier).isEqualTo(TypeQualifier(TestComponent2::class))
    }

    @Test
    fun `if a child scope has the same type, it takes precedence over the parent`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            single<String> { "ParentValue" }
            scope<TestComponent1> {
                single<String> { "ChildValue" }
            }
        }

        // when
        val actual =
            scope
                .getSubScope(TypeQualifier(TestComponent1::class))
                .get(TypeQualifier(String::class))

        // then
        assertThat(actual).isEqualTo("ChildValue")
    }

    @Test
    fun `if the parent scope is closed, the child scope cannot resolve the parent's dependencies`() {
        // given
        val rootScope = ScopeImpl(testQualifier)
        combine(rootScope) {
            single { Child2() }
            scope<TestComponent1> {
                single { NestedDependency(get()) }
            }
        }

        val childScope = rootScope.getSubScope(TypeQualifier(TestComponent1::class))

        // when
        rootScope.closeAll()

        // then
        assertThatThrownBy {
            childScope.get(TypeQualifier(NestedDependency::class))
        }.hasMessageContaining("Cannot find instance in container")
    }
}
