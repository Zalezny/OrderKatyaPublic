package com.example.orderappkatya.interfaces

interface Communicator {
    fun passDataCom(isArchive: Boolean, nrOrder: Int, pos: Int)
}