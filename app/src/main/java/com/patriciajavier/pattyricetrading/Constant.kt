package com.patriciajavier.pattyricetrading

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Constant {

    val TEXT_FIELD_ERROR_MSG : String = "* Required"
    val TEXT_FIELD_ERROR_FORMAT_MSG : String = "* Invalid format"
    val PASSWORD_FIELD_ERROR_MSG : String = "Password must contain one digit from 1 to 9, one lowercase letter, one uppercase letter, no special character, no space, and it must be 8-16 characters long."

    val PHONE_NUMBER_VALIDATION : String = "(((\\+63)(\\d){10}\$)|(^\\d{11}\$)|(^\\d{12}\$))"
    val EMAIL_ADDRESS_VALIDATION : String = "^[a-z0-9][a-z0-9-_\\.]+@([a-z]|[a-z0-9]?[a-z0-9-]+[a-z0-9])\\.[a-z0-9]{2,10}(?:\\.[a-z]{2,10})?\$"
    val PASSWORD_VALIDATION : String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*\\W)(?!.* ).{8,16}\$"

    //admin
    val CARD_TITLE_ACCOUNT : String = "Accounts"
    val CARD_TITLE_INVENTORY : String = "Stock"
    val CARD_TITLE_ORDER : String = "Orders"
    val CARD_TITLE_SALES_REPORT : String = "Sales Report"

    //shopkeeper
    val CARD_TITLE_SALES : String = "Sack Sales"
    val CARD_TITLE_SALES_KILO : String = "Kilo Sales"
    val CARD_TITLE_TRANSACTION : String = "Past Transactions"

    val YEAR_MONTH_DAY : String = "yyyy-MM-dd"
    val YEAR_MONTH_DAY_HOUR_MINUTE_SECOND : String = "yyyy-MM-dd HH:mm:ss"

    // convert timeStamp to GMT+8:00
    fun timeStampToGMT8(timestamp: Timestamp, timeFormat: String): String{
        val timeStamp = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val dateFormat = SimpleDateFormat(timeFormat)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val netDate = Date(timeStamp)
        return dateFormat.format(netDate).toString()
    }

    // get the a custom date
    fun getCalculatedDate(dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        s.timeZone = TimeZone.getTimeZone("GMT+8:00")
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }
}