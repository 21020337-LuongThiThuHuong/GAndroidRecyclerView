package com.example.gandroidrecyclerview

data class OrderModel(
    var name: String,
    var status: Boolean = false,
    var phone: String,
    var address: String,
    var category: String,
    var codPrice: Int,
    var note: ArrayList<String>
)