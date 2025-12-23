package mole.example.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mole.android.scope.AndroidScopes
import com.mole.android.util.autoViewModels
import com.mole.android.util.viewModelScope
import com.mole.core.LazyBind
import com.mole.core.ScopeComponent
import mole.example.R
import mole.example.databinding.ActivityMainBinding
import mole.example.ui.cart.CartActivity

class MainActivity :
    AppCompatActivity(),
    ScopeComponent<AndroidScopes.ActivityScope> by LazyBind() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModelScope = viewModelScope<MainViewModel>()

    private val viewModel by autoViewModels<MainViewModel>(viewModelScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupContentView()
        setupBinding()
        setupToolbar()
        setupViewData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        menu?.findItem(R.id.cart)?.actionView?.let { view ->
            view.setOnClickListener { navigateToCart() }
        }
        return true
    }

    private fun setupContentView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupViewData() {
        setupProductData()
        setupProductList()
    }

    private fun setupProductData() {
        viewModel.getAllProducts()
    }

    private fun setupProductList() {
        viewModel.products.observe(this) {
            val adapter =
                ProductAdapter(
                    items = it,
                    onClickProduct = viewModel::addCartProduct,
                )
            binding.rvProducts.adapter = adapter
        }
        viewModel.onProductAdded.observe(this) {
            if (!it) return@observe
            Toast.makeText(this, getString(R.string.cart_added), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }
}
