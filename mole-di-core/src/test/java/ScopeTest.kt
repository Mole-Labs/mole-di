import com.mole.core.module.combine
import com.mole.core.qualifier.TypeQualifier
import com.mole.core.qualifier.annotated
import com.mole.core.qualifier.named
import com.mole.core.scope.ScopeImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.thread

class ScopeTest {
    @Test
    fun `get는 등록된 팩토리를 통해 객체를 생성해야 한다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
    fun `중첩 의존성 체인을 성공적으로 해결하고 주입해야 한다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
    fun `factory로 등록한 의존성은 매번 다른 인스턴스를 생성한다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
    fun `single로 등록한 의존성은 동일 인스턴스를 반환한다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
    fun `순환 참조가 발생하면 예외를 던진다`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            single { CircularDependency1(get()) }
            single { CircularDependency2(get()) }
        }

        // when - then
        assertThatThrownBy {
            scope.get(
                TypeQualifier(CircularDependency1::class),
            )
        }.message().contains("순환 참조가 발견되었습니다")
    }

    @Test
    fun `멀티 스레드 환경에서 순환 참조가 발생해도 예외를 던진다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
        }.message().contains("순환 참조가 발견되었습니다")
    }

    @Test
    fun `필수 의존성을 해결할 수 없으면 예외를 던진다`() {
        // given
        val scope = ScopeImpl(testQualifier)
        combine(scope) {
            single { Parent(get(), get()) }
        }

        // when - then
        assertThatThrownBy {
            scope.get(
                TypeQualifier(Parent::class),
            )
        }.message().contains("컨테이너에서 인스턴스를 찾을 수 없습니다")
    }

    @Test
    fun `같은 타입을 네이밍으로 구분하여 생성자 주입을 수행할 수 있다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
    fun `같은 타입을 어노테이션으로 구분하여 생성자 주입을 수행할 수 있다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
    fun `동시에 같은 스레드에서 요청해도 한 번만 객체가 생성된다`() {
        // given
        val scope = ScopeImpl(testQualifier)
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
