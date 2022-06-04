package com.example.indikascam.util

import java.text.SimpleDateFormat
import java.util.*

class Util() {

    fun stringToDate(stringDate: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
        return sdf.parse(stringDate)!!
    }

}