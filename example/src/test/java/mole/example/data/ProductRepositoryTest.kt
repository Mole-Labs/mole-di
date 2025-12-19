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
    fun `모든 상품을 불러올 수 있다`() {
        // given
        val expected =
            listOf(
                "우테코 과자",
                "우테코 쥬스",
                "우테코 아이스크림",
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
