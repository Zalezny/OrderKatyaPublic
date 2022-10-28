package com.example.orderappkatya.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {
    private val _body = MutableLiveData<String>().apply {
        value = ""
    }
    val body : LiveData<String> = _body
    fun setBody(body: String) = _body.postValue(body)
}