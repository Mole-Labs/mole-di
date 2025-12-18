package com.daedan.di.path

import com.daedan.di.qualifier.Qualifier

class Path {
    private val _order: MutableList<Qualifier>

    constructor() {
        _order = arrayListOf()
    }

    constructor(initialValue: Qualifier) {
        _order = arrayListOf(initialValue)
    }

    val order: List<Qualifier> get() = _order.toList()

    fun append(qualifier: Qualifier) {
        _order.add(0, qualifier)
    }
}
