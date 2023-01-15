package com.example.orderappkatya.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.orderappkatya.datamodels.ActiveDataModel
import com.example.katya.orders.recyclers.OrdersAdapter
import com.example.orderappkatya.ItemActivity
import com.example.orderappkatya.datamodels.OrderDataModel
import com.example.orderappkatya.interfaces.Communicator
import com.example.orderappkatya.interfaces.RefreshCom
import com.example.orderappkatya.ConstDatabase
import com.example.orderappkatya.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import okhttp3.*
import java.io.IOException
import kotlinx.coroutines.*

private const val ARG_PARAM1 = "param1"

class CurrentFragment : Fragment() {


    private lateinit var activeRecyclerView: RecyclerView
    private lateinit var communicator: Communicator
    private lateinit var refreshCom: RefreshCom
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var currAdapter : OrdersAdapter
    private lateinit var currRestartTextView : TextView
    private lateinit var currentLimitRequestTextView : TextView
    private lateinit var currRestartButton : Button

    private var lastPosition : Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current, container, false)

        //init interface communicator for swipeRefreshLayout to refresh data from other fragment
        communicator = activity as Communicator
        refreshCom = activity as RefreshCom
        initRecyclerView(view)



        //refresh layout
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
                getOrdersData()
                //To refresh Data in ArchiveFragments (send argument):
                refreshCom.refreshFragment(false)
            }, 1000)
        }

        currRestartButton.setOnClickListener { getOrdersData() }



        getOrdersData()

        return view
    }

    private fun initRecyclerView(view: View) {
        //get a reference to recyclerView
        activeRecyclerView = view.findViewById<RecyclerView>(R.id.current_recycler_view_fragment)
        //set layoutManager
        layoutManager = LinearLayoutManager(activity)
        activeRecyclerView.layoutManager = layoutManager
        //
        activeRecyclerView.setHasFixedSize(true)
        //set progress bar
        progressBar = view.findViewById<ProgressBar>(R.id.progress_bar_current)
        //TextView when is need restart
        currRestartTextView = view.findViewById(R.id.current_text_view_restart_no_orders)
        //Button when is need restart
        currRestartButton = view.findViewById(R.id.current_restart_button_no_orders)

        currentLimitRequestTextView = view.findViewById(R.id.current_text_view_limit_request)


    }

    private fun getOrdersData()  {
        //List for order of active/current
         val orderArrayListActive: ArrayList<ActiveDataModel> = arrayListOf()

        //Use library okHttpClient to take body from JSON
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(ConstDatabase.ORDER_URL)
            .addHeader("authorization", ConstDatabase.KEY)
            .build()

        progressBar.visibility = View.VISIBLE
        currRestartButton.visibility = View.GONE
        currRestartTextView.visibility = View.GONE
        currentLimitRequestTextView.visibility = View.GONE

        //Function is ansych, so we have to predicted this
        okHttpClient.newCall(request).enqueue(object : Callback {
            //when will be Fail
            override fun onFailure(call: Call, e: IOException) {
                Log.d(javaClass.name, "Response: $e")
                Toast.makeText(activity, "Failure request data", Toast.LENGTH_SHORT).show()
            }

            //when will be Response, function asynch!!!
            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                Log.d("body", "Body is $body")
                if(response.message.isNullOrBlank()  && (!body.isNullOrBlank())) {

                    response.apply {
                        if (!response.isSuccessful) {

                            activity!!.runOnUiThread{ currentLimitRequestTextView.visibility = View.VISIBLE }

                            throw IOException("Unexpected code $response")
                        }


                        //Create intent and put here body for ItemActivity if will be open
                        val intent = Intent(context, ItemActivity::class.java)

                        //gson - change body in objects/arrays
                        val gson = GsonBuilder().create()
                        val gsonMainArray = gson.fromJson(body, Array<OrderDataModel>::class.java)


                        //While which take from JSON Array of orders
                        for (i in gsonMainArray.size - 1 downTo 0) {
                            val orderObj = gsonMainArray[i]
                            //if is current order
                            if (!orderObj.archive) {

                                val orderNumber = orderObj.orderNumber

                                //set name
                                val firstName = orderObj.name
                                val lastName = orderObj.lastName
                                val fullName = "$firstName $lastName"

                                //set format of date
                                val dateNonFormat = orderObj.date

                                //set price - take total price from orders inside of array
                                val ordersInside = orderObj.order
                                var totalPrice = 0.0F
                                for (j in 0 until ordersInside.size) {
                                    val numberOrder = ordersInside[j]
                                    val priceOrder = numberOrder.price
                                    val quantityOrder = numberOrder.quantity
                                    totalPrice += priceOrder * quantityOrder
                                }

                                val kindOfShipment = orderObj.shipment.method["kind"]
                                val phone = orderObj.phone
                                val isNew = orderObj.newOrder

                                val id = orderObj._id

                                //Add all variables to ActiveDataModel for RecyclerView
                                val orderDetails = ActiveDataModel(
                                    orderNumber,
                                    fullName,
                                    dateNonFormat,
                                    totalPrice,
                                    kindOfShipment,
                                    phone,
                                    intent,
                                    isNew,
                                    id,
                                )
                                //Add  orderDetails to ArrayList for Current orders
                                if (!isNew)
                                    orderArrayListActive.add(orderDetails)
                                else
                                    orderArrayListActive.add(0, orderDetails)
                            }
                            //for archive orders

                        }
                        //because it is asynch function and view cannot be here, we take this to new ui thread
                        activity?.runOnUiThread {
                            currAdapter = OrdersAdapter(orderArrayListActive)
                            activeRecyclerView.adapter = currAdapter
                            progressBar.visibility = View.GONE
                            activeRecyclerView.scrollToPosition(lastPosition)
                            //From lambda - OrdersAdapter
                            currAdapter.listener = {
                                activity!!.startActivityForResult(intent, 1)
                            }
                        }


                    }
                }
                else {
                    activity!!.runOnUiThread {
                        progressBar.visibility = View.GONE
                        currRestartButton.visibility = View.VISIBLE
                        currRestartTextView.visibility = View.VISIBLE
                    }

                }
            }


        })

    }

    //To receive data from other fragment
    fun removeData( pos: Int) {
            currAdapter.removeItem(pos)

    }
    fun refreshData(){
        getOrdersData()
    }
}