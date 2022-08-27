package com.patriciajavier.pattyricetrading.home.admin.market.kilo

import android.widget.Toast
import androidx.core.view.isGone
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.ProductMarketCardModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.ProductPerKg
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class ProductMarketKiloEpoxyController(
    private val onItemClick: (String) -> Unit,
    private val onRefillClick: (String) -> Unit,
    private val onUpdateClick: (String) -> Unit
) : EpoxyController(){

    var products : List<ProductPerKg>? = emptyList()
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
                onItemClick,
                onUpdateClick,
                onRefillClick
            ).id(product.id).addTo(this)
        }

    }
}

data class ProductMarketPerSackModel(
    val product: ProductPerKg,
    val onItemClick: (String) -> Unit,
    val onUpdateClick: (String) -> Unit,
    val onRefillClick: (String) -> Unit
): ViewBindingKotlinModel<ProductMarketCardModelBinding>(R.layout.product_market_card_model){
    override fun ProductMarketCardModelBinding.bind() {
        root.setOnClickListener {
            if(product.quantity == 0){
                Toast.makeText(MyApp.appContext, "Not enough stock of ${product.productName}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onItemClick(product.id)
        }

        updatePrice.setOnClickListener {
            onUpdateClick(product.id)
        }

        refillDisplay.setOnClickListener {
            onRefillClick(product.id)
        }

//        currentStock.isGone = true

        Glide.with(MyApp.appContext)
            .load(product.productImage)
            .into(riceImage)

        if(product.quantity > product.capacity){
            riceVariation.isGone = false
            riceVariation.text = "Overflow by: ${product.capacity - product.quantity}kg"
            riceVariation.isEnabled = false
        } else{
            riceVariation.isGone = true
            riceVariation.isEnabled = true
        }

        riceTitle.text = product.productName
        unitPrice.text = product.unitPrice.toInt().toString()
        refillCapLabel.text = "Refill Cap: ${product.quantity}/${product.capacity}"
    }
}