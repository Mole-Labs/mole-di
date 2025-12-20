package mole.example.di

import com.mole.android.dsl.module.viewModel
import com.mole.core.module.ModuleDefinition
import com.mole.core.qualifier.annotated
import mole.example.ui.cart.CartViewModel

fun viewModelModule(): ModuleDefinition =
    {
        viewModel { CartViewModel(get(annotated<RoomDBCartRepository>())) }
    }
