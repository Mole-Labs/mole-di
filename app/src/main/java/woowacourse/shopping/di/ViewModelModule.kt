package woowacourse.shopping.di

import com.daedan.di.module.ModuleDefinition
import com.daedan.di.util.annotated
import woowacourse.shopping.ui.cart.CartViewModel

fun viewModelModule(): ModuleDefinition =
    {
        viewModel { CartViewModel(get(annotated<RoomDBCartRepository>())) }
    }
