package mole.example.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mole.example.databinding.ItemProductBinding
import mole.example.domain.model.Product

class ProductViewHolder(
    private val binding: ItemProductBinding,
    onClickProduct: (Product) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            onClickProduct(binding.item ?: return@setOnClickListener)
        }
    }

    fun bind(product: Product) {
        binding.item = product
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onClickProduct: (Product) -> Unit,
        ): ProductViewHolder {
            val binding =
                ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductViewHolder(binding, onClickProduct)
        }
    }
}
