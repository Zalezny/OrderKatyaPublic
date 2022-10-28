package com.example.orderappkatya.datamodels

import android.content.Intent

data class ActiveDataModel (var numberOfOrder : Int ?= null,
                           var nameOfCustomer : String ?= null,
                           var dateEx : String ?= null,
                           var priceEx : Float ?= null,
                           var deliveryEx : String ?= null,
                           var phoneEx : String ?= null,
                            var intent : Intent ?= null,
                            var isNew : Boolean = false,
                            var id : String ?= null,
                            var visibility : Boolean = false,


)
//intent for global putExtra()

