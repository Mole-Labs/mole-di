package com.daedan.di

import com.daedan.di.dsl.DependencyModuleBuilder

interface DiComponent {
    val rootScope: Scope

    fun root(block: DependencyModuleBuilder.() -> Unit): DependencyModule = module(rootScope, block)
}
