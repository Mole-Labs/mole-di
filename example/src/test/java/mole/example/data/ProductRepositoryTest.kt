package mole.example.data

import mole.example.data.repository.DefaultProductRepository
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class ProductRepositoryTest {
    private lateinit var productRepository: DefaultProductRepository

    @Before
    fun setup() {
        productRepository = DefaultProductRepository()
    }

    @Test
    fun `can load all products`() {
        // given
        val expected =
            listOf(
                "Woowahan Snack",
                "Woowahan Juice",
                "Woowahan Ice Cream",
            )

        // when
        val actual =
            productRepository.getAllProducts().map {
                it.name
            }

        // then
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
