package com.daedan.di

import com.daedan.di.annotation.Component
import com.daedan.di.annotation.Inject
import com.daedan.di.module.DependencyModule
import com.daedan.di.module.InstanceDependencyFactory
import com.daedan.di.module.ScopeDependencyFactory
import com.daedan.di.qualifier.CreateRule
import com.daedan.di.qualifier.Qualifier
import com.daedan.di.qualifier.TypeQualifier
import com.daedan.di.util.getQualifier
import java.util.Collections
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class Scope(
    val qualifier: Qualifier,
    val parent: Scope? = null,
) {
    private val cache = mutableMapOf<Qualifier, Any>()

    private val factory = mutableMapOf<Qualifier, InstanceDependencyFactory<*>>()
    private val children = mutableMapOf<Qualifier, ScopeDependencyFactory>()

    fun declare(
        qualifier: Qualifier,
        instance: Any,
    ) {
        if (cache.containsKey(qualifier)) error("$ERR_CONFLICT_KEY : $qualifier")
        cache[qualifier] = instance
    }

    fun registerFactory(vararg modules: DependencyModule) {
        val newFactories = modules.flatMap { it.factories }

        // Map으로 변환
        val newFactoryMap = mutableMapOf<Qualifier, InstanceDependencyFactory<*>>()
        val newScopeMap = mutableMapOf<Qualifier, ScopeDependencyFactory>()

        newFactories.forEach { factory ->
            val key = factory.qualifier
            when (factory) {
                is InstanceDependencyFactory<*> -> {
                    require(!newFactoryMap.containsKey(key)) { ERR_CONFLICT_KEY }
                    newFactoryMap[key] = factory
                }

                is ScopeDependencyFactory -> {
                    require(!newScopeMap.containsKey(key)) { ERR_CONFLICT_KEY }
                    newScopeMap[key] = factory
                }
            }
        }

        val conflictingKeys =
            newFactoryMap.keys.filter {
                factory.containsKey(it)
            }
        require(conflictingKeys.isEmpty()) {
            "$ERR_CONFLICT_KEY ${conflictingKeys.joinToString()}"
        }

        children.putAll(newScopeMap)
        factory.putAll(newFactoryMap)
    }

    // 자식 스코프에서 실행
    fun closeAll() {
        cache.clear()
    }

    fun get(qualifier: Qualifier): Any {
        val inProgress = Collections.synchronizedSet(mutableSetOf<Qualifier>())

        return runCatching {
            get(qualifier, inProgress)
        }.getOrNull() ?: parent?.get(qualifier, inProgress)
            ?: error("$ERR_CANNOT_FIND_INSTANCE : $qualifier")
    }

    fun getSubScope(qualifier: Qualifier): Scope {
        synchronized(this) {
            return children[qualifier]?.invoke()
                ?: error("$ERR_CONSTRUCTOR_NOT_FOUND : $qualifier")
        }
    }

    inline fun <reified T : Any> getSubScope(): Scope = getSubScope(TypeQualifier(T::class))

    private fun get(
        qualifier: Qualifier,
        inProgress: MutableSet<Qualifier>,
    ): Any {
        if (cache.containsKey(qualifier)) {
            return cache[qualifier] ?: error("$ERR_CANNOT_FIND_INSTANCE : $qualifier")
        }

        val creator = factory[qualifier] ?: error("$ERR_CONSTRUCTOR_NOT_FOUND : $qualifier")

        synchronized(this) {
            // 성능 이점을 위하여 락의 범위를 세분화함에 따라 더블 체킹 로직을 수행합니다
            if (inProgress.contains(qualifier)) {
                error("$ERR_CIRCULAR_DEPENDENCY_DETECTED : $qualifier")
            }

            inProgress.add(qualifier)
            try {
                val instance = creator.invoke()
                injectField(instance)
                save(qualifier, instance)
                return instance
            } finally {
                inProgress.remove(qualifier)
            }
        }
    }

    private fun injectField(instance: Any) {
        try {
            instance::class.memberProperties
        } catch (e: Error) {
            // 코틀린 리플렉션이 지원하지 않는 프레임워크 객체는 건너뜁니다
            if (e::class.simpleName == "KotlinReflectionInternalError") return
        }
        instance::class
            .memberProperties
            .filterIsInstance<KMutableProperty1<Any, Any>>()
            .filter { it.isTargetField() }
            .forEach { property ->
                val childQualifier = property.getQualifier()
                property.isAccessible = true
                property.set(
                    instance,
                    get(childQualifier),
                )
            }
    }

    private fun KMutableProperty1<*, *>.isTargetField(): Boolean =
        findAnnotation<Inject>() != null ||
            annotations.any {
                it.annotationClass.findAnnotation<Component>() != null
            }

    private fun save(
        qualifier: Qualifier,
        instance: Any,
    ) {
        val createRule =
            factory[qualifier]?.createRule ?: error("$ERR_CONSTRUCTOR_NOT_FOUND : $qualifier")
        when (createRule) {
            CreateRule.SINGLE -> cache[qualifier] = instance
            CreateRule.FACTORY -> Unit
        }
    }

    companion object {
        private const val ERR_CONFLICT_KEY = "이미 동일한 Qualifier가 존재합니다"
        private const val ERR_CANNOT_FIND_INSTANCE = "컨테이너에서 인스턴스를 찾을 수 없습니다"
        private const val ERR_CIRCULAR_DEPENDENCY_DETECTED = "순환 참조가 발견되었습니다"
        private const val ERR_CONSTRUCTOR_NOT_FOUND =
            "등록된 팩토리, 또는 주 생성자를 찾을 수 없습니다"
    }
}
