package com.patriciajavier.pattyricetrading.home.admin.inventory.order

import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.InventoryCardRiceModelBinding
import com.patriciajavier.pattyricetrading.databinding.OrderCardModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.CardRiceModel
import com.patriciajavier.pattyricetrading.firestore.models.Order
import com.patriciajavier.pattyricetrading.firestore.models.OrderCompleteInfo
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderModuleEpoxyController(
    private val onItemClick: (String) -> Unit,
    private val onOrderClick: (String, String, String) -> Unit,
    private val onCancelOrder: (String, String, String) -> Unit
) : EpoxyController(){

    var accessRights: Boolean = false

    var response : List<Product>? = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    var orders : List<OrderCompleteInfo>? = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {

        if(accessRights){
            orders?.forEach{ orders ->
                OrderCardModel(
                    orders,
                    onOrderClick,
                    onCancelOrder
                ).id(orders.oId).addTo(this)
            }
        }else{
            response?.forEach { product ->
                CardRiceModel(
                    product,
                    onItemClick,
                    accessRights
                ).id(product.pId).addTo(this)
            }
        }
    }
}

data class OrderCardModel(
    val orders: OrderCompleteInfo,
    val onOrderClick: (String, String, String) -> Unit,
    val onCancelOrder: (String, String, String) -> Unit
):ViewBindingKotlinModel<OrderCardModelBinding>(R.layout.order_card_model){
    override fun OrderCardModelBinding.bind() {
        acceptOrder.setOnClickListener {
            onOrderClick(orders.oId, orders.pId!!.pId, orders.uId!!.uId)
        }

        declineOrder.setOnClickListener {
            onCancelOrder(orders.oId, orders.pId!!.pId, orders.uId!!.uId)
        }

        Glide.with(MyApp.appContext)
            .load(orders.pId!!.productImage)
            .into(riceImage)

        riceTitle.text = orders.pId.productName
        description.text = "Requested by: ${orders.uId!!.firstName} ${orders.uId.lastName}"
        dateOrdered.text = "${orders.dateOrdered}"
        unitPrice.text = "(Qty:${orders.qTy}) P${orders.totalCost}"
        riceVariation.text = "Variation: ${orders.variation}kg"
        currentStock.text = "Stock: ${orders.pId.stock}"
    }
}

