package com.patriciajavier.pattyricetrading

object Constant {

    val TEXT_FIELD_ERROR_MSG : String = "* Required"
    val TEXT_FIELD_ERROR_FORMAT_MSG : String = "* Invalid format"
    val PASSWORD_FIELD_ERROR_MSG : String = "Password must contain one digit from 1 to 9, one lowercase letter, one uppercase letter, no special character, no space, and it must be 8-16 characters long."

    val PHONE_NUMBER_VALIDATION : String = "((^(\\+)(\\d){12}\$)|(^\\d{11}\$))"
    val EMAIL_ADDRESS_VALIDATION : String = "^[a-z0-9][a-z0-9-_\\.]+@([a-z]|[a-z0-9]?[a-z0-9-]+[a-z0-9])\\.[a-z0-9]{2,10}(?:\\.[a-z]{2,10})?\$"
    val PASSWORD_VALIDATION : String = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*\\W)(?!.* ).{8,16}\$"
}