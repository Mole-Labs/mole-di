package mole.example.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mole.android.scope.AndroidScopes
import com.mole.core.scope.LazyBind
import com.mole.core.scope.ScopeComponent
import kotlinx.coroutines.launch
import mole.example.domain.model.Product
import mole.example.domain.repository.CartRepository

class CartViewModel(
    private val cartRepository: CartRepository,
) : ViewModel(),
    ScopeComponent<AndroidScopes.ViewModelScope> by LazyBind() {
    private val _cartProducts: MutableLiveData<List<Product>> =
        MutableLiveData(emptyList())
    val cartProducts: LiveData<List<Product>> get() = _cartProducts

    private val _onCartProductDeleted: MutableLiveData<Boolean> = MutableLiveData(false)
    val onCartProductDeleted: LiveData<Boolean> get() = _onCartProductDeleted

    fun getAllCartProducts() {
        viewModelScope.launch {
            _cartProducts.value = cartRepository.getAllCartProducts()
        }
    }

    fun deleteCartProduct(id: Int) {
        viewModelScope.launch {
            cartRepository.deleteCartProduct(id)
        }
        _onCartProductDeleted.value = true
    }
}
