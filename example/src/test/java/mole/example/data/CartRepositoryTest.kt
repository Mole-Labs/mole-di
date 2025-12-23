package mole.example.data

import kotlinx.coroutines.test.runTest
import mole.example.data.repository.DefaultCartRepository
import mole.fixture.FakeCartProductDao
import mole.fixture.PRODUCT1
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {
    private lateinit var cartRepository: DefaultCartRepository

    @Before
    fun setup() {
        cartRepository =
            DefaultCartRepository(
                cartProductDao = FakeCartProductDao(),
            )
    }

    @Test
    fun `can delete a product`() =
        runTest {
            // given
            cartRepository.addCartProduct(PRODUCT1)
            val target = cartRepository.getAllCartProducts().first()
            val id = 0

            // when
            cartRepository.deleteCartProduct(id)

            // then
            val actual = cartRepository.getAllCartProducts()
            Assertions.assertThat(actual).doesNotContain(target)
        }

    @Test
    fun `can add a product`() =
        runTest {
            // given
            val product = PRODUCT1

            // when
            cartRepository.addCartProduct(product)

            // then
            val actual = cartRepository.getAllCartProducts()
            Assertions.assertThat(actual).anyMatch { it.id == product.id }
        }

    @Test
    fun `can retrieve products`() =
        runTest {
            // given
            val product = PRODUCT1
            cartRepository.addCartProduct(product)
            val expected = listOf("Product Name")

            // when
            cartRepository.getAllCartProducts()

            // then
            val actual = cartRepository.getAllCartProducts().map { it.name }
            Assertions.assertThat(actual).isEqualTo(expected)
        }
}
