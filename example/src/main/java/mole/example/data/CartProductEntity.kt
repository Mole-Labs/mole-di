package mole.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_products")
data class CartProductEntity(
    val name: String,
    val price: Int,
    val imageUrl: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var createdAt: Long = System.currentTimeMillis()
}
