package com.patriciajavier.pattyricetrading.home.admin.market

import com.airbnb.epoxy.EpoxyController
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.TotalModelProductMarketBinding
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel
import java.util.*

class TotalEpoxyController: EpoxyController(){

    var product : List<Product?> = emptyList()
    set(value){
        field = value
        requestModelBuild()
    }

    override fun buildModels() {
        product.forEach {
            TotalModel(it!!).id(UUID.randomUUID().toString()).addTo(this)
        }
    }
}

data class TotalModel(
    val product: Product
    ): ViewBindingKotlinModel<TotalModelProductMarketBinding>(R.layout.total_model_product_market){
    override fun TotalModelProductMarketBinding.bind() {
        textView16.text = product.productName
        textView17.text = "Qty:(${product.qty}) P${product.unitPrice * product.qty}"
    }

}