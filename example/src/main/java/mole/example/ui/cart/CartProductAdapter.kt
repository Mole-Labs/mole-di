package mole.example.ui.cart

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mole.example.domain.model.Product

class CartProductAdapter(
    items: List<Product>,
    onClickDelete: (id: Int) -> Unit,
    private val dateFormatter: DateFormatter,
) : RecyclerView.Adapter<CartProductViewHolder>() {
    private val items: MutableList<Product> = items.toMutableList()

    private val onClickDelete = { id: Int ->
        onClickDelete(id)
        removeItem(id)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CartProductViewHolder = CartProductViewHolder.from(parent, dateFormatter, onClickDelete)

    override fun onBindViewHolder(
        holder: CartProductViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private fun removeItem(id: Int) {
        notifyItemRemoved(items.indexOfFirst { it.id == id })
        items.removeIf { it.id == id }
    }
}
