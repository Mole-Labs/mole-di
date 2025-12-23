package com.mole.core.scope

import com.mole.core.module.DependencyModule
import com.mole.core.path.Path
import com.mole.core.qualifier.Qualifier

interface Scope {
    /** 인스턴스 조회 */
    fun get(qualifier: Qualifier): Any

    /** 자식 스코프 획득 */
    fun getSubScope(qualifier: Qualifier): Scope

    /** 경로를 통한 하위 스코프 탐색 */
    fun resolvePath(path: Path): Scope

    fun declare(
        qualifier: Qualifier,
        instance: Any,
    )

    /** 의존성 모듈 등록 */
    fun registerFactory(vararg modules: DependencyModule)

    /** 스코프 종료 및 자원 해제 */
    fun closeAll()
}
