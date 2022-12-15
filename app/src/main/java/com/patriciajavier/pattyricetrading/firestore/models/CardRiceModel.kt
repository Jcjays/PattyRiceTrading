package com.patriciajavier.pattyricetrading.firestore.models

import android.content.res.ColorStateList
import android.drm.DrmRights
import android.graphics.Color
import com.bumptech.glide.Glide
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.InventoryCardRiceModelBinding
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

data class CardRiceModel(
    // variables
    val product: Product,
    val onItemClick: (String) -> Unit,
    val accessRights: Boolean = true

): ViewBindingKotlinModel<InventoryCardRiceModelBinding>(R.layout.inventory_card_rice_model){
    override fun InventoryCardRiceModelBinding.bind() {
        root.setOnClickListener {
            onItemClick(product.pId)
        }

        Glide.with(MyApp.appContext)
            .load(product.productImage)
            .into(riceImage)

        if(!accessRights){
            availabilityStatus.text = if(product.stock > 0) "Available" else "Out of stock"
            availabilityStatus.setTextColor(if(product.stock > 0) Color.GREEN else Color.RED)
        }

        riceVariation.text = "Variation: ${product.kiloPerSack} kg"
        riceTitle.text = product.productName
        riceStockCount.text = "Stocks left: ${product.stock}"
        riceUnitPrice.text = "Unit price: ${product.unitPrice}"
        riceDescription.text ="description:  ${product.productDesc}"
    }
}