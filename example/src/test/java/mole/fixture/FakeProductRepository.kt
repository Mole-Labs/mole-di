package mole.fixture

import mole.example.domain.model.Product
import mole.example.domain.repository.ProductRepository

class FakeProductRepository(
    private val fakeAllProducts: List<Product>,
) : ProductRepository {
    override fun getAllProducts(): List<Product> = fakeAllProducts
}
