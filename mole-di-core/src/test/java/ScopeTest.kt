import com.mole.core.module.combine
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.qualifier.annotated
import com.mole.core.qualifier.named
import com.mole.core.scope.DefaultScope
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class ScopeTest {
    @Test
    fun `get should create an object through a registered factory`() {
        // given
        val scope = DefaultScope(testQualifier)
        val qualifier = TypeQualifier(Parent::class)
        combine(scope) {
            single { Parent(Child1(), Child2()) }
        }

        // when
        val actual = scope.get(qualifier)

        // then
        assertThat(actual).isInstanceOf(Parent::class.java)
    }

    @Test
    fun `should successfully resolve and inject a nested dependency chain`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single { Child1() }
            single { Child2() }
            single { Parent(child1 = get(), child2 = get()) }
            single { NestedDependency(get()) }
        }

        // when
        val actual =
            scope.get(
                TypeQualifier(NestedDependency::class),
            )

        // then
        assertThat(actual).isInstanceOf(NestedDependency::class.java)
    }

    @Test
    fun `a dependency registered as a factory creates a different instance each time`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            factory { Child1() }
        }

        // when
        val expected = scope.get(TypeQualifier(Child1::class))
        val actual = scope.get(TypeQualifier(Child1::class))

        // then
        assertThat(actual).isNotSameAs(expected)
    }

    @Test
    fun `a dependency registered as a single returns the same instance`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single { Child1() }
            single { Child2() }
            single { Parent(child1 = get(), child2 = get()) }
        }

        // when - then
        val actual =
            scope.get(
                TypeQualifier(Parent::class),
            )
        val expected =
            scope.get(
                TypeQualifier(Parent::class),
            )

        // then
        assertThat(actual).isSameAs(expected)
    }

    @Test
    fun `throws an exception when a circular dependency occurs`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single { CircularDependency1(get()) }
            single { CircularDependency2(get()) }
        }

        // when - then
        assertThatThrownBy {
            scope.get(
                TypeQualifier(CircularDependency1::class),
            )
        }.message().contains("Circular dependency detected")
    }

    @Test
    fun `throws an exception even if a circular dependency occurs in a multi-threaded environment`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single { CircularDependency1(get()) }
            single { CircularDependency2(get()) }
        }

        // when - then
        assertThatThrownBy {
            val t1 =
                CompletableFuture.supplyAsync {
                    scope.get(
                        TypeQualifier(CircularDependency1::class),
                    )
                }

            val t2 =
                CompletableFuture.supplyAsync {
                    scope.get(
                        TypeQualifier(CircularDependency1::class),
                    )
                }

            t1.join()
            t2.join()
        }.message().contains("Circular dependency detected")
    }

    @Test
    fun `throws an exception if a required dependency cannot be resolved`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single { Parent(get(), get()) }
        }

        // when - then
        assertThatThrownBy {
            scope.get(
                TypeQualifier(Parent::class),
            )
        }.message().contains("Cannot find instance in container")
    }

    @Test
    fun `can perform constructor injection by distinguishing the same type by naming`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single(named("child1")) { Child1() }
            single(named("child2")) { Child1() }
            single { SameDependencyParent(get(named("child1")), get(named("child2"))) }
        }

        // when - then
        assertThatCode {
            scope.get(
                TypeQualifier(SameDependencyParent::class),
            ) as SameDependencyParent
        }.doesNotThrowAnyException()
    }

    @Test
    fun `can perform constructor injection by distinguishing the same type by annotation`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single(annotated<TestComponent1>()) { Child1() }
            single(annotated<TestComponent2>()) { Child1() }
            single {
                SameDependencyParent(
                    get(annotated<TestComponent1>()),
                    get(annotated<TestComponent2>()),
                )
            }
        }

        // when - then
        assertThatCode {
            scope.get(
                TypeQualifier(SameDependencyParent::class),
            ) as SameDependencyParent
        }.doesNotThrowAnyException()
    }

    @Test
    fun `even if requested from the same thread at the same time, the object is created only once`() {
        // given
        val scope = DefaultScope(testQualifier)
        combine(scope) {
            single { Child1() }
            single { Child2() }
            single { Parent(get(), get()) }
        }
        var actual1: Parent? = null
        var actual2: Parent? = null

        // when
        val thread1 =
            thread {
                actual1 = scope.get(TypeQualifier(Parent::class)) as Parent
            }
        val thread2 =
            thread {
                actual2 = scope.get(TypeQualifier(Parent::class)) as Parent
            }
        thread1.join()
        thread2.join()

        // then
        assertThat(actual1).isSameAs(actual2)
    }
}
