package mole.example.domain.repository

import mole.example.domain.model.Product

interface ProductRepository {
    fun getAllProducts(): List<Product>
}
