package com.patriciajavier.pattyricetrading

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTime.formatWithPattern(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}