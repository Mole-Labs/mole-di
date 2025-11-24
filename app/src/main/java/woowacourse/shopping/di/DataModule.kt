package woowacourse.shopping.di

import android.content.Context
import androidx.room.Room
import com.daedan.di.module.ModuleDefinition
import woowacourse.shopping.data.ShoppingDatabase

fun dataModule(applicationContext: Context): ModuleDefinition =
    {
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
