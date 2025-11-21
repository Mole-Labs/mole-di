package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daedan.di.util.autoViewModels
import com.daedan.di.util.getRootScope
import com.daedan.di.util.inject
import com.daedan.di.util.registerActivityRetainedLifecycle
import com.daedan.di.util.registerActivityScope
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }

    private val viewModel by autoViewModels<CartViewModel>()

    private val dateFormatter by inject<DateFormatter>(
        scope = getRootScope().getSubScope<CartActivity>(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        registerActivityScope()
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
        registerActivityRetainedLifecycle()
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
