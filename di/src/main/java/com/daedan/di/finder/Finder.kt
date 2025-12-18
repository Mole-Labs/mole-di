package com.daedan.di.finder

import com.daedan.di.qualifier.Qualifier

data class Finder(
    val qualifier: Qualifier,
    val next: Finder? = null,
)
