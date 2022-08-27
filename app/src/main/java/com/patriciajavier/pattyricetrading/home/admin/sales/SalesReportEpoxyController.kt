package com.patriciajavier.pattyricetrading.home.admin.sales

import com.airbnb.epoxy.EpoxyController
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.TransactionCardButtonModelBinding
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import com.patriciajavier.pattyricetrading.registration.arch.ViewBindingKotlinModel
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class SalesReportEpoxyController : EpoxyController(){

    var logs : List<Logs>? = emptyList()
    set(value) {
        field = value
        requestModelBuild()
    }

    override fun buildModels() {
            logs?.forEach {
                SalesReportCardModel(
                    it
                ).id(it.transactionId).addTo(this)
            }
    }
}

data class SalesReportCardModel(
    val logs : Logs
): ViewBindingKotlinModel<TransactionCardButtonModelBinding>(R.layout.transaction_card_button_model){
    override fun TransactionCardButtonModelBinding.bind() {

        if(MyApp.accessRights){
            textView15.text = "Transaction ID: ${logs.transactionId}"
            textView11.text = "${logs.productName} to ${logs.customerName}"
            textView12.text = Constant.timeStampToGMT8(logs.timeCreated, Constant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
            textView14.text = "+ P${logs.totalCost}"
            textView13.text = "P${logs.unitPrice} (${logs.quantity} qty.)"
        }else{
            textView15.text = "Transaction ID: ${logs.transactionId}"
            textView11.text = if(logs.isFromMarket) "Sold: ${logs.productName}" else "Received: ${logs.productName}"
            textView12.text = Constant.timeStampToGMT8(logs.timeCreated, Constant.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
            textView14.text = if(logs.isFromMarket) "+ P${logs.totalCost}" else "- P${logs.totalCost}"
            textView13.text = "P${logs.unitPrice} (${logs.quantity} qty.)"
        }

    }
}