package mole.example.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mole.example.domain.model.Product

class ProductAdapter(
    private val items: List<Product>,
    private val onClickProduct: (Product) -> Unit,
) : RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProductViewHolder = ProductViewHolder.from(parent, onClickProduct)

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
