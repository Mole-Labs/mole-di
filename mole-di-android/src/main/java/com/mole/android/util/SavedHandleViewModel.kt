package com.mole.android.util

import androidx.lifecycle.ViewModel
import com.mole.android.scope.AndroidScopes

internal class SavedHandleViewModel : ViewModel() {
    var scope: AndroidScopes.ActivityRetainedScope? = null
}
