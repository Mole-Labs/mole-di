package woowacourse.shopping.di

import com.daedan.di.DependencyModule
import com.daedan.di.DiComponent
import com.daedan.di.util.annotated
import woowacourse.shopping.ui.MainViewModel
import woowacourse.shopping.ui.cart.CartViewModel

fun DiComponent.viewModelModule(): DependencyModule =
    root {
        viewModel { CartViewModel(get(annotated<RoomDBCartRepository>())) }
        viewModel { MainViewModel() }
    }
