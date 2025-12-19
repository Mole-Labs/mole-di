package mole.example.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mole.android.AppContainerStore
import com.mole.android.module.combine
import com.mole.android.qualifier.NamedQualifier
import com.mole.android.qualifier.annotated
import com.mole.android.qualifier.named
import com.mole.android.util.getQualifier
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
        val appContainerStore = AppContainerStore()
        val module =
            combine(appContainerStore) {
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

                viewModel { MainViewModel() }
            }
        appContainerStore.registerFactory(module)

        productRepository =
            appContainerStore.instantiate(NamedQualifier("productRepository")) as ProductRepository
        viewModel =
            appContainerStore.instantiate(MainViewModel::class.getQualifier()) as MainViewModel
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `상품을 추가할 수 있다`() =
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
    fun `모든 상품을 조회할 수 있다`() {
        // given
        val expected = productRepository.getAllProducts()

        // when
        viewModel.getAllProducts()

        // then
        val actual = viewModel.products.getOrAwaitValue()
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
