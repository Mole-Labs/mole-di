package mole.example

import android.app.Application
import com.mole.core.qualifier.RootScopeQualifier
import com.mole.core.scope.DefaultScope
import com.mole.core.scope.RootComponent
import mole.example.di.dataModule
import mole.example.di.dateFormatterModule
import mole.example.di.repositoryModule
import mole.example.di.viewModelModule

class MainApplication :
    Application(),
    RootComponent {
    override val scope = DefaultScope(RootScopeQualifier)

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
