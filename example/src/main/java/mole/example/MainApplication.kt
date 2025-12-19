package mole.example

import android.app.Application
import com.mole.android.DiComponent
import com.mole.android.qualifier.RootScopeQualifier
import com.mole.android.scope.Scope
import mole.example.di.dataModule
import mole.example.di.dateFormatterModule
import mole.example.di.repositoryModule
import mole.example.di.viewModelModule

class MainApplication :
    Application(),
    DiComponent {
    override val rootScope = Scope(RootScopeQualifier)

    override fun onCreate() {
        super.onCreate()
        combineToRoot(
            dateFormatterModule(),
            dataModule(this@MainApplication),
            repositoryModule(),
            viewModelModule(),
        )
    }
}
