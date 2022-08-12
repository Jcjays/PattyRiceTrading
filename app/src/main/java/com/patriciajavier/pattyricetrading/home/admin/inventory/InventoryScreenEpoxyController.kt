package com.patriciajavier.pattyricetrading.home.admin.inventory

import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.InventoryCardRiceModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class InventoryScreenEpoxyController: EpoxyController() {

    var response : List<Product>? = emptyList()
    set(value) {
        field = value

        requestModelBuild()
    }

    override fun buildModels() {

        response?.forEach { product ->
            CardRiceModel(
                product.productName,
                product.productImage,
                product.stock,
                product.unitPrice
            ).id(product.pId).addTo(this)
        }
    }

    data class CardRiceModel(
        val title: String,
        val image: String,
        val stock: Int,
        val price:  Double
    ): ViewBindingKotlinModel<InventoryCardRiceModelBinding>(R.layout.inventory_card_rice_model){
        override fun InventoryCardRiceModelBinding.bind() {
            root.setOnClickListener {
                //todo
            }

            Glide.with(MyApp.appContext)
                .load(image)
                .into(riceImage)

            riceTitle.text = title
            riceStockCount.text = "Stocks left: $stock"
            riceUnitPrice.text = "Unit price: $price"
        }
    }
}