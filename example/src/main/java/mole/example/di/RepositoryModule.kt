package mole.example.di

import com.mole.android.dsl.module.viewModel
import com.mole.android.dsl.module.viewModelScope
import com.mole.core.module.ModuleDefinition
import com.mole.core.qualifier.annotated
import com.mole.core.qualifier.named
import mole.example.data.repository.DefaultCartRepository
import mole.example.data.repository.DefaultProductRepository
import mole.example.domain.repository.CartRepository
import mole.example.domain.repository.ProductRepository
import mole.example.ui.MainViewModel

fun repositoryModule(): ModuleDefinition =
    {
        single<CartRepository>(annotated<RoomDBCartRepository>()) {
            DefaultCartRepository(
                get(),
            )
        }
        viewModelScope<MainViewModel> {
            single<ProductRepository>(named("productRepository")) {
                DefaultProductRepository()
            }
            viewModel {
                MainViewModel(
                    get(named("productRepository")),
                    get(annotated<RoomDBCartRepository>()),
                )
            }
        }
    }
