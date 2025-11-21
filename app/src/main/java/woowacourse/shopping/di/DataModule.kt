package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import com.daedan.di.DiComponent
import woowacourse.shopping.data.ShoppingDatabase

fun DiComponent.dataModule(applicationContext: Context) =
    root {
        single {
            Room
                .databaseBuilder(
                    applicationContext,
                    ShoppingDatabase::class.java,
                    "shopping_db",
                ).build()
        }
        single { get<ShoppingDatabase>().cartProductDao() }
    }
