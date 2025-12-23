import com.mole.core.dsl.Root
import com.mole.core.module.combine
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.scope.ScopeImpl
import com.mole.core.scope.get
import com.mole.core.scope.subScope
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ScopeExtTest {
    @Test
    fun `can access a scope through Path DSL`() {
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
            scope.subScope { find of scope<TestComponent2>() of scope<TestComponent1>() of Root }

        // then
        assertThat(actual.qualifier).isEqualTo(TypeQualifier(TestComponent2::class))
    }

    @Test
    fun `can create an object through Path DSL`() {
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
            scope.get<NestedDependency> { find of scope<TestComponent2>() of scope<TestComponent1>() of Root }

        // then
        assertThat(actual).isInstanceOf(NestedDependency::class.java)
    }
}
