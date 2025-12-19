package mole.example.di

import com.mole.android.module.ModuleDefinition
import com.mole.android.qualifier.annotated
import mole.example.ui.cart.CartViewModel

fun viewModelModule(): ModuleDefinition =
    {
        viewModel { CartViewModel(get(annotated<RoomDBCartRepository>())) }
    }
