package com.patriciajavier.pattyricetrading.home.admin.inventory

import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.InventoryCardRiceModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class InventoryScreenEpoxyController(
    private val onItemClick: (String) -> Unit
): EpoxyController() {

    var response : List<Product>? = emptyList()
    set(value) {
        field = value

        requestModelBuild()
    }

    override fun buildModels() {

        response?.forEach { product ->
            CardRiceModel(
                product,
                onItemClick
            ).id(product.pId).addTo(this)
        }
    }

    data class CardRiceModel(
        val product: Product,
        val onItemClick: (String) -> Unit
    ): ViewBindingKotlinModel<InventoryCardRiceModelBinding>(R.layout.inventory_card_rice_model){
        override fun InventoryCardRiceModelBinding.bind() {
            root.setOnClickListener {
                onItemClick(product.pId)
            }

            Glide.with(MyApp.appContext)
                .load(product.productImage)
                .into(riceImage)

            riceTitle.text = product.productName
            riceStockCount.text = "Stocks left: ${product.stock}"
            riceUnitPrice.text = "Unit price: ${product.unitPrice}"
        }
    }
}