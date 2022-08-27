package com.patriciajavier.pattyricetrading.home.admin

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import com.airbnb.epoxy.EpoxyController
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.DashboardCardButtonModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel

class AdminDashboardEpoxyController(
    private val clickListener: (String) -> Unit
) : EpoxyController() {

    //to determine what UI should it display
    var userAccess : Boolean = false
    set(value) {
        field = value
        requestModelBuild()
    }

    var userPriviledge : Boolean = true


    override fun buildModels() {

        if(userAccess){
            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_ACCOUNT,
                icon = R.drawable.ic_accounts,
                userPriviledge,
                clickListener
            ).id("account_id").addTo(this)

            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_SALES_REPORT,
                icon = R.drawable.ic_sales_report,
                userPriviledge,
                clickListener
            ).id("sales_id").addTo(this)

            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_INVENTORY,
                icon = R.drawable.ic_inventory,
                userPriviledge,
                clickListener
            ).id("inventory_id").addTo(this)
        }else{

            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_SALES,
                icon = R.drawable.ic_point_of_sale,
                userPriviledge,
                clickListener
            ).id("point_of_sales_id").addTo(this)

            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_SALES_KILO,
                icon = R.drawable.ic_shopping_bag_24,
                userPriviledge,
                clickListener

            ).id("per_kg_product_market").addTo(this)

            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_INVENTORY,
                icon = R.drawable.ic_inventory,
                userPriviledge,
                clickListener
            ).id("inventory_id").addTo(this)

            DashboardCardButtonModel(
                cardTitle = Constant.CARD_TITLE_TRANSACTION,
                icon = R.drawable.ic_receipt_long,
                userPriviledge,
                clickListener
            ).id("transaction_id").addTo(this)

        }
    }

    data class DashboardCardButtonModel(
        val cardTitle: String,
        val icon: Int,
        val access: Boolean,
        val clickListener: (String) -> Unit
    ) : ViewBindingKotlinModel<DashboardCardButtonModelBinding>(R.layout.dashboard_card_button_model) {
        override fun DashboardCardButtonModelBinding.bind() {
            root.isEnabled = access

            root.setOnClickListener {
                clickListener(cardTitle)
            }

            iconTitle.text = cardTitle
            dashboardImageButton.setImageResource(icon)
        }
    }
}