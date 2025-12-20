package com.mole.android.util

import androidx.lifecycle.ViewModel
import com.mole.core.scope.Scope

internal class SavedHandleViewModel : ViewModel() {
    var scope: Scope? = null
}
