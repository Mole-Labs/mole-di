package woowacourse.shopping.di

import com.daedan.di.module.ModuleDefinition
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.cart.DateFormatter

fun dateFormatterModule(): ModuleDefinition =
    {
        activityRetainedScope<CartActivity> {
            activityScope<CartActivity> {
                single { DateFormatter(get()) }
            }
        }
    }
