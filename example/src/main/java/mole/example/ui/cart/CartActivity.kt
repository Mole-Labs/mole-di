package mole.example.ui.cart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mole.android.scope.AndroidScopes
import com.mole.android.scope.inject
import com.mole.android.util.activityScope
import com.mole.android.util.autoViewModels
import com.mole.core.ScopeComponent
import com.mole.core.dsl.Root
import mole.example.R
import mole.example.databinding.ActivityCartBinding

class CartActivity :
    AppCompatActivity(),
    ScopeComponent<AndroidScopes.ActivityScope> {
    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }

    private val viewModel by autoViewModels<CartViewModel>()

    override val scope =
        activityScope {
            find of activityRetainedScope<CartActivity>() of Root
        }

    private val dateFormatter by scope.inject<DateFormatter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupContentView()
        setupBinding()
        setupToolbar()
        setupViewData()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun setupViewData() {
        setupCartProductData()
        setupCartProductList()
    }

    private fun setupCartProductData() {
        viewModel.getAllCartProducts()
    }

    private fun setupCartProductList() {
        viewModel.cartProducts.observe(this) {
            val adapter =
                CartProductAdapter(
                    items = it,
                    dateFormatter = dateFormatter,
                    onClickDelete = viewModel::deleteCartProduct,
                )
            binding.rvCartProducts.adapter = adapter
        }
        viewModel.onCartProductDeleted.observe(this) {
            if (!it) return@observe
            Toast.makeText(this, getString(R.string.cart_deleted), Toast.LENGTH_SHORT).show()
        }
    }
}
