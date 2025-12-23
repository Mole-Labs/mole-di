import com.mole.core.module.combine
import com.mole.core.path.Path
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.ScopeImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class NestedScopeTest {
    @Test
    fun `scope를 통해 등록한 스코프의 인스턴스를 가져올 수 있다`() {
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
    fun `중첩해서 scope를 적용할 수 있다`() {
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
    fun `한 스코프에서 다른 스코프에 등록된 의존성을 찾을 수 없다`() {
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
        }.message().contains("컨테이너에서 인스턴스를 찾을 수 없습니다")
    }

    @Test
    fun `한 스코프에서 부모 스코프에 등록된 의존성을 찾을 수 있다`() {
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
    fun `Path를 통해 스코프를 찾을 수 있다`() {
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
    fun `자식 스코프에 동일한 타입이 있으면 부모보다 우선한다`() {
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
    fun `부모 스코프가 close되면 자식 스코프에서 부모의 의존성을 조회할 수 없다`() {
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
        }.hasMessageContaining("컨테이너에서 인스턴스를 찾을 수 없습니다")
    }
}
