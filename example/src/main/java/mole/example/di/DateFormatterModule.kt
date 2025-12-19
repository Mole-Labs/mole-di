package mole.example.di

import com.mole.android.module.ModuleDefinition
import mole.example.ui.cart.CartActivity
import mole.example.ui.cart.DateFormatter

fun dateFormatterModule(): ModuleDefinition =
    {
        activityRetainedScope<CartActivity> {
            activityScope<CartActivity> {
                single { DateFormatter(get()) }
            }
        }
    }
