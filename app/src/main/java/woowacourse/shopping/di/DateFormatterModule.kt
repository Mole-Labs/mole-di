package woowacourse.shopping.di

import com.daedan.di.DiComponent
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.cart.DateFormatter

fun DiComponent.dateFormatterModule() =
    root {
        scope<CartActivity> {
            single { DateFormatter(get()) }
        }
    }
