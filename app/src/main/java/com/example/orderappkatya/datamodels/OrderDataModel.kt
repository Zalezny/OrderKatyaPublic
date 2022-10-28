package com.example.orderappkatya.datamodels

data class OrderDataModel (
    val shipment: Shipment,
    val _id : String,
    val orderNumber : Int,
    val name: String,
    val lastName: String,
    val street: String,
    val postCode: String,
    val city: String,
    val phone: String,
    val email: String,
    val comments: String,
    val order: Array<Order>,
    val archive: Boolean,
    val newOrder : Boolean,
    val date: String,
    val _v: Int
)

class Order(
    val title: String,
    val color: List<String>,
    val quantity: Int,
    val photo: String,
    val _id: String,
    val price: Float,
    val size : String? = null,
)

class Shipment(
    val method: Map<String, String>,
    val point: Map<String, String>,
//    val price: Float,
)