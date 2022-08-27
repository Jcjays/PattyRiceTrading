package com.patriciajavier.pattyricetrading.home.admin.market

import androidx.core.view.isGone
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.ProductMarketCardModelBinding
import com.patriciajavier.pattyricetrading.databinding.ProductMarketPersackModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class ProductMarketEpoxyController(
    private val onItemClick : (String) -> Unit
) : EpoxyController() {

var products : List<Product>? = emptyList()
    set(value) {
        field = value
        requestModelBuild()
    }

    override fun buildModels() {
        if(products.isNullOrEmpty())
            return

        products!!.forEach { product ->
            ProductMarketPerSackModel(
                product,
                onItemClick
            ).id(product.pId).addTo(this)
        }

    }
}

data class ProductMarketPerSackModel(
    val product: Product,
    val onItemClick: (String) -> Unit
): ViewBindingKotlinModel<ProductMarketPersackModelBinding>(R.layout.product_market_persack_model){
    override fun ProductMarketPersackModelBinding.bind() {
        root.setOnClickListener {
            onItemClick(product.pId)
        }

        root.isEnabled = product.stock != 0

        Glide.with(MyApp.appContext)
            .load(product.productImage)
            .into(riceImage)

        riceTitle.text = product.productName
        unitPrice.text = product.unitPrice.toInt().toString()
        currentStock.text = "Stock: ${product.stock}"
        riceVariation.text = "Variation: ${product.kiloPerSack}kg"
    }
}