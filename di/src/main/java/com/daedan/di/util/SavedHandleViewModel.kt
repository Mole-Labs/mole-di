package com.daedan.di.util

import androidx.lifecycle.ViewModel
import com.daedan.di.scope.Scope

internal class SavedHandleViewModel : ViewModel() {
    var scope: Scope? = null
}
