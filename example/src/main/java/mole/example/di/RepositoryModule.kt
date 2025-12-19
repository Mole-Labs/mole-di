package mole.example.di

import com.mole.android.module.ModuleDefinition
import com.mole.android.qualifier.annotated
import com.mole.android.qualifier.named
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
