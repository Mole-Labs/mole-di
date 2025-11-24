package woowacourse.shopping

import android.app.Application
import com.daedan.di.DiComponent
import com.daedan.di.Scope
import com.daedan.di.qualifier.RootScopeQualifier
import woowacourse.shopping.di.dataModule
import woowacourse.shopping.di.dateFormatterModule
import woowacourse.shopping.di.repositoryModule
import woowacourse.shopping.di.viewModelModule

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
