package mole.example.data.repository

import mole.example.data.CartProductDao
import mole.example.data.mapper.toEntity
import mole.example.data.mapper.toProduct
import mole.example.domain.model.Product
import mole.example.domain.repository.CartRepository

class DefaultCartRepository(
    private val cartProductDao: CartProductDao,
) : CartRepository {
    override suspend fun addCartProduct(product: Product) {
        cartProductDao.insert(product.toEntity())
    }

    override suspend fun getAllCartProducts(): List<Product> = cartProductDao.getAll().map { it.toProduct() }

    override suspend fun deleteCartProduct(id: Int) {
        cartProductDao.delete(id.toLong())
    }
}
