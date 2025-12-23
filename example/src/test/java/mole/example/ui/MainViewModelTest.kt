package mole.example.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mole.android.dsl.module.viewModel
import com.mole.core.module.combine
import com.mole.core.qualifier.NamedQualifier
import com.mole.core.qualifier.annotated
import com.mole.core.qualifier.named
import com.mole.core.scope.DefaultScope
import com.mole.core.scope.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mole.example.di.RoomDBCartRepository
import mole.example.domain.repository.CartRepository
import mole.example.domain.repository.ProductRepository
import mole.fixture.FakeCartRepository
import mole.fixture.FakeProductRepository
import mole.fixture.PRODUCT1
import mole.fixture.getOrAwaitValue
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: MainViewModel
    private lateinit var productRepository: ProductRepository

    @Before
    fun setup() {
        val scope =
            DefaultScope(
                NamedQualifier("MainViewModelTest"),
            )
        combine(scope) {
            single<ProductRepository>(named("productRepository")) {
                FakeProductRepository(
                    fakeAllProducts =
                        listOf(
                            PRODUCT1,
                        ),
                )
            }

            single<CartRepository>(annotated<RoomDBCartRepository>()) {
                FakeCartRepository(
                    fakeAllCartProducts =
                        listOf(
                            PRODUCT1,
                        ),
                )
            }

            viewModel {
                MainViewModel(
                    get(named("productRepository")),
                    get(annotated<RoomDBCartRepository>()),
                )
            }
        }

        productRepository =
            scope.get(named("productRepository")) as ProductRepository
        viewModel =
            scope.get<MainViewModel>() as MainViewModel
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `can add a product`() =
        runTest {
            // given
            val product = PRODUCT1

            // when
            viewModel.addCartProduct(product)
            advanceUntilIdle()

            // then
            val productAdded = viewModel.onProductAdded.getOrAwaitValue()
            Assertions.assertThat(productAdded).isTrue()
        }

    @Test
    fun `can retrieve all products`() {
        // given
        val expected = productRepository.getAllProducts()

        // when
        viewModel.getAllProducts()

        // then
        val actual = viewModel.products.getOrAwaitValue()
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
