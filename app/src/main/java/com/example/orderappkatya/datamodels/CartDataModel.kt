package com.example.orderappkatya.datamodels

data class CartDataModel(val title: String,
                         val color: List<String>? = null,
                         val photoUrl: String,
                         val quantity: Int,
                         val price: Float,
                         val size: String? = null
                         )
