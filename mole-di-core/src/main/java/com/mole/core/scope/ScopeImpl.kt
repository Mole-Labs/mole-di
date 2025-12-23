package com.mole.core.scope

import com.mole.core.module.DependencyModule
import com.mole.core.module.InstanceDependencyFactory
import com.mole.core.module.ScopeDependencyFactory
import com.mole.core.path.Path
import com.mole.core.qualifier.CreateRule
import com.mole.core.qualifier.Qualifier
import java.util.concurrent.ConcurrentHashMap

class ScopeImpl(
    val qualifier: Qualifier,
    val parent: ScopeImpl? = null,
) : Scope {
    private val cache = ConcurrentHashMap<Qualifier, Any>()

    private val inProgress = ThreadLocal.withInitial { mutableSetOf<Qualifier>() }

    private val factory = ConcurrentHashMap<Qualifier, InstanceDependencyFactory<*>>()
    private val children = ConcurrentHashMap<Qualifier, ScopeDependencyFactory>()

    override fun declare(
        qualifier: Qualifier,
        instance: Any,
    ) {
        if (cache.containsKey(qualifier)) error("$ERR_CONFLICT_KEY : $qualifier")
        cache[qualifier] = instance
    }

    override fun registerFactory(vararg modules: DependencyModule) {
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
    override fun closeAll() {
        cache.clear()
    }

    override fun getSubScope(qualifier: Qualifier): ScopeImpl =
        children[qualifier]?.invoke()
            ?: error("$ERR_CONSTRUCTOR_NOT_FOUND : $qualifier")

    override fun resolvePath(path: Path): ScopeImpl {
        var current = this
        for (qualifier in path.order) {
            current = current.getSubScope(qualifier)
        }
        return current
    }

    override fun get(qualifier: Qualifier): Any {
        cache[qualifier]?.let { return it }
        if (inProgress.get().contains(qualifier)) {
            error("$ERR_CIRCULAR_DEPENDENCY_DETECTED : $qualifier")
        }

        inProgress.get().add(qualifier)
        val creator =
            factory[qualifier] ?: return parent?.get(qualifier)
                ?: error("$ERR_CANNOT_FIND_INSTANCE : $qualifier")

        try {
            return save(qualifier, creator)
        } finally {
            inProgress.get().remove(qualifier)
        }
    }

    private fun save(
        qualifier: Qualifier,
        creator: InstanceDependencyFactory<*>,
    ): Any {
        val createRule =
            factory[qualifier]?.createRule ?: error("$ERR_CONSTRUCTOR_NOT_FOUND : $qualifier")
        return when (createRule) {
            CreateRule.SINGLE ->
                cache.computeIfAbsent(qualifier) { _ -> creator() }

            CreateRule.FACTORY -> {
                creator()
            }
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
