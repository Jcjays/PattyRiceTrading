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


    override fun buildModels() {

        DashboardCardButtonModel(
            cardTitle = Constant.CARD_TITLE_ACCOUNT,
            icon = R.drawable.ic_accounts,
            clickListener
        ).id("account_id").addTo(this)

        DashboardCardButtonModel(
            cardTitle = Constant.CARD_TITLE_SALES_REPORT,
            icon = R.drawable.ic_sales_report,
            clickListener
        ).id("sales_id").addTo(this)

        DashboardCardButtonModel(
            cardTitle = Constant.CARD_TITLE_INVENTORY,
            icon = R.drawable.ic_inventory,
            clickListener
        ).id("inventory_id").addTo(this)
    }


    data class DashboardCardButtonModel(
        val cardTitle: String,
        val icon: Int,
        val clickListener: (String) -> Unit
    ) : ViewBindingKotlinModel<DashboardCardButtonModelBinding>(R.layout.dashboard_card_button_model) {
        override fun DashboardCardButtonModelBinding.bind() {
            root.setOnClickListener {
                clickListener(cardTitle)
            }

            iconTitle.text = cardTitle
            dashboardImageButton.setImageResource(icon)
        }
    }
}