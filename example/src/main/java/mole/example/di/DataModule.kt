package mole.example.di

import android.content.Context
import androidx.room.Room
import com.mole.android.module.ModuleDefinition
import mole.example.data.ShoppingDatabase

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
