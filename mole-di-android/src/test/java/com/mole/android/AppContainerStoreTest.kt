package com.mole.android

import com.mole.android.fixture.Child1
import com.mole.android.fixture.Child2
import com.mole.android.fixture.CircularDependency1
import com.mole.android.fixture.CircularDependency2
import com.mole.android.fixture.ComponentObject1
import com.mole.android.fixture.ComponentObject2
import com.mole.android.fixture.ConstructorInjectionWithAnnotation
import com.mole.android.fixture.ConstructorInjectionWithName
import com.mole.android.fixture.FieldAndConstructorInjection
import com.mole.android.fixture.FieldInjection
import com.mole.android.fixture.FieldInjectionWithAnnotation
import com.mole.android.fixture.FieldInjectionWithName
import com.mole.android.fixture.GeneralAnnotation
import com.mole.android.fixture.NestedDependency
import com.mole.android.fixture.Parent
import com.mole.android.fixture.TestComponent1
import com.mole.android.fixture.TestComponent2
import com.mole.android.fixture.UnableReflectObject
import com.mole.android.module.combine
import com.mole.android.qualifier.TypeQualifier
import com.mole.android.qualifier.annotated
import com.mole.android.qualifier.named
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import kotlin.concurrent.thread

class AppContainerStoreTest {
    @Test
    fun `instantiate는 등록된 팩토리를 통해 객체를 생성해야 한다`() {
        // given
        val scope = Scope()
        val qualifier = TypeQualifier(Parent::class)
        val module =
            combine(scope) {
                single { Parent(Child1(), Child2()) }
            }
        scope.registerFactory(module)

        // when
        val actual = scope.get(qualifier)

        // then
        assertThat(actual).isInstanceOf(Parent::class.java)
    }

    @Test
    fun `중첩 의존성 체인을 성공적으로 해결하고 주입해야 한다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single { Parent(child1 = get(), child2 = get()) }
                single { NestedDependency(get()) }
            }
        scope.registerFactory(module)

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
        val scope = Scope()
        val module =
            combine(scope) {
                factory { Child1() }
            }
        scope.registerFactory(module)

        // when
        val expected = scope.get(TypeQualifier(Child1::class))
        val actual = scope.get(TypeQualifier(Child1::class))

        // then
        assertThat(actual).isNotSameAs(expected)
    }

    @Test
    fun `single로 등록한 의존성은 동일 인스턴스를 반환한다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single { Parent(child1 = get(), child2 = get()) }
            }
        scope.registerFactory(module)

        // when
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
        val scope = Scope()
        val module =
            combine(scope) {
                single { CircularDependency1(get()) }
                single { CircularDependency2(get()) }
                single { Parent(get(), get()) }
            }

        // when
        scope.registerFactory(module)

        // then
        assertThatThrownBy {
            scope.get(
                TypeQualifier(CircularDependency1::class),
            )
        }.message().contains("순환 참조가 발견되었습니다")
    }

    @Test
    fun `필수 의존성을 해결할 수 없으면 예외를 던진다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { UnableReflectObject(get()) }
            }

        // when
        scope.registerFactory(module)

        // then
        assertThatThrownBy {
            scope.get(
                TypeQualifier(UnableReflectObject::class),
            )
        }.message().contains("주 생성자를 찾을 수 없습니다")
    }

    @Test
    fun `@Component가 없는 어노테이션은 인스턴스를 등록하지 않는다`() {
        // given
        val scope = Scope()
        val obj1 = ComponentObject1()

        // when - then
        assertThatThrownBy {
            combine(scope) {
                single(annotated<GeneralAnnotation>()) { obj1 }
            }
        }.message().contains("@Component 어노테이션으로 등록되지 않았습니다")
    }

    @Test
    fun `필드 주입을 수행할 수 있다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single { FieldInjection() }
            }

        // when
        scope.registerFactory(module)

        // then
        val obj =
            scope.get(TypeQualifier(FieldInjection::class)) as FieldInjection
        obj.assertPropertyInitialized()
    }

    @Test
    fun `같은 타입을 네이밍으로 구분하여 필드 주입을 수행할 수 있다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single(named("parent1")) { Parent(get(), get()) }
                single(named("parent2")) { Parent(get(), get()) }
                single { FieldInjectionWithName() }
            }

        // when
        scope.registerFactory(module)

        // then
        val obj =
            scope.get(
                TypeQualifier(FieldInjectionWithName::class),
            ) as FieldInjectionWithName

        obj.assertPropertyInitialized()
    }

    @Test
    fun `같은 타입을 어노테이션으로 구분하여 필드 주입을 수행할 수 있다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single(annotated<TestComponent1>()) { ComponentObject1() }
                single(annotated<TestComponent2>()) { ComponentObject2() }
                single { FieldInjectionWithAnnotation() }
            }

        // when
        scope.registerFactory(module)

        // then
        val obj =
            scope.get(
                TypeQualifier(FieldInjectionWithAnnotation::class),
            ) as FieldInjectionWithAnnotation

        obj.assertPropertyInitialized()
    }

    @Test
    fun `같은 타입을 네이밍으로 구분하여 생성자 주입을 수행할 수 있다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single { Parent(get(), get()) }
                single { ConstructorInjectionWithName(get(), get()) }
            }

        // when
        scope.registerFactory(module)

        // then
        assertThatCode {
            scope.get(
                TypeQualifier(ConstructorInjectionWithName::class),
            ) as ConstructorInjectionWithName
        }.doesNotThrowAnyException()
    }

    @Test
    fun `같은 타입을 어노테이션으로 구분하여 생성자 주입을 수행할 수 있다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single(annotated<TestComponent1>()) { ComponentObject1() }
                single(annotated<TestComponent2>()) { ComponentObject2() }
                single {
                    ConstructorInjectionWithAnnotation(
                        get(annotated<TestComponent1>()),
                        get(annotated<TestComponent2>()),
                    )
                }
            }
        scope.registerFactory(module)

        // when - then
        assertThatCode {
            scope.get(
                TypeQualifier(ConstructorInjectionWithAnnotation::class),
            ) as ConstructorInjectionWithAnnotation
        }.doesNotThrowAnyException()
    }

    @Test
    fun `필드 주입과 생성자 주입을 동시에 수행할 수 있다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single(annotated<TestComponent1>()) { ComponentObject1() }
                single(annotated<TestComponent2>()) { ComponentObject2() }
                single(named("parent1")) { Parent(get(), get()) }
                single(named("parent2")) { Parent(get(), get()) }

                single {
                    FieldAndConstructorInjection(
                        get(named("parent1")),
                        get(annotated<TestComponent2>()),
                    )
                }
            }
        // when
        scope.registerFactory(module)

        // then
        assertThatCode {
            scope.get(
                TypeQualifier(FieldAndConstructorInjection::class),
            ) as FieldAndConstructorInjection
        }.doesNotThrowAnyException()
    }

    @Test
    fun `동시에 같은 스레드에서 요청해도 한 번만 객체가 생성된다`() {
        // given
        val scope = Scope()
        val module =
            combine(scope) {
                single { Child1() }
                single { Child2() }
                single { Parent(get(), get()) }
            }
        scope.registerFactory(module)
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
