package mole.example.data.mapper

import mole.example.data.CartProductEntity
import mole.example.domain.model.Product

fun Product.toEntity(): CartProductEntity =
    CartProductEntity(
        name = name,
        price = price,
        imageUrl = imageUrl,
    )

fun CartProductEntity.toProduct(): Product =
    Product(
        id = id.toInt(),
        name = name,
        price = price,
        imageUrl = imageUrl,
        createdAt = createdAt,
    )
