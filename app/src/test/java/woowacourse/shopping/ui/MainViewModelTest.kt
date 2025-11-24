package woowacourse.shopping.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.daedan.di.AppContainerStore
import com.daedan.di.module.combine
import com.daedan.di.qualifier.NamedQualifier
import com.daedan.di.util.annotated
import com.daedan.di.util.getQualifier
import com.daedan.di.util.named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.fixture.FakeCartRepository
import woowacourse.fixture.FakeProductRepository
import woowacourse.fixture.PRODUCT1
import woowacourse.fixture.getOrAwaitValue
import woowacourse.shopping.di.RoomDBCartRepository
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

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
