package com.daedan.di.util

import com.daedan.di.annotation.Component
import com.daedan.di.annotation.Inject
import com.daedan.di.qualifier.AnnotationQualifier
import com.daedan.di.qualifier.NamedQualifier
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

fun KClass<*>.getQualifier(): Qualifier = resolveQualifier(this)

fun KMutableProperty1<*, *>.getQualifier(): Qualifier {
    val defaultType = returnType.jvmErasure
    return resolveQualifier(defaultType)
}

inline fun <reified T : Annotation> annotated(): AnnotationQualifier = AnnotationQualifier(T::class)

fun named(name: String): NamedQualifier = NamedQualifier(name)

/**
 * 어노테이션 정보를 기반으로 Qualifier를 결정하는 핵심 로직.
 * @param defaultType 어노테이션이 발견되지 않았거나 이름이 없을 경우 사용할 기본 타입 (KClass).
 * @return 결정된 Qualifier 객체.
 */
private fun KAnnotatedElement.resolveQualifier(defaultType: KClass<*>): Qualifier {
    val inject = findAnnotation<Inject>()
    val component =
        annotations.find {
            it.annotationClass.findAnnotation<Component>() != null
        }

    return when {
        // 우선 순위 1: @Component 메타 어노테이션을 가진 어노테이션이 있는 경우
        component != null -> AnnotationQualifier(component.annotationClass)

        // 우선 순위 2: @Inject가 있는 경우
        inject != null -> {
            if (inject.name.isNotEmpty()) {
                NamedQualifier(inject.name)
            } else {
                // 이름이 없으면 인자로 받은 기본 타입 사용
                TypeQualifier(defaultType)
            }
        }

        // 우선 순위 3: 아무것도 없는 경우
        else -> TypeQualifier(defaultType)
    }
}
