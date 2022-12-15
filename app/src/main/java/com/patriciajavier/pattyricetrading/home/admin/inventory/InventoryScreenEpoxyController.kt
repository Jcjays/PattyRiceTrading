package com.patriciajavier.pattyricetrading.home.admin.inventory

import com.airbnb.epoxy.EpoxyController
import com.patriciajavier.pattyricetrading.firestore.models.CardRiceModel
import com.patriciajavier.pattyricetrading.firestore.models.Product

class InventoryScreenEpoxyController(
    private val onItemClick: (String) -> Unit
): EpoxyController() {

    var response : List<Product>? = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {

        if(response!!.isNotEmpty()){

            response?.forEach { product ->
                CardRiceModel(
                    product,
                    onItemClick
                ).id(product.pId).addTo(this)
            }

        }

    }
}