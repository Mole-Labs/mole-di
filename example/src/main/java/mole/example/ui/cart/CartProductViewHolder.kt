package mole.example.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mole.example.databinding.ItemCartProductBinding
import mole.example.domain.model.Product

class CartProductViewHolder(
    private val binding: ItemCartProductBinding,
    private val dateFormatter: DateFormatter,
    onClickDelete: (id: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var product: Product

    init {
        binding.ivCartProductDelete.setOnClickListener {
            onClickDelete(product.id)
        }
    }

    fun bind(product: Product) {
        this.product = product
        binding.item = product
        binding.tvCartProductCreatedAt.text = dateFormatter.formatDate(product.createdAt) // added
    }

    companion object {
        fun from(
            parent: ViewGroup,
            dateFormatter: DateFormatter,
            onClickDelete: (position: Int) -> Unit,
        ): CartProductViewHolder {
            val binding =
                ItemCartProductBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            return CartProductViewHolder(binding, dateFormatter, onClickDelete)
        }
    }
}
