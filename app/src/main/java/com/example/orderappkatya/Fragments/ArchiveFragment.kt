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

class ArchiveFragment : Fragment() {


    private lateinit var archiveRecyclerView: RecyclerView
    private lateinit var communicator: Communicator
    private lateinit var refreshCom: RefreshCom
    private lateinit var archiveProgressBar: ProgressBar
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: OrdersAdapter
    private lateinit var archiveRestartTextView: TextView
    private lateinit var archiveLimitRequestTextView: TextView
    private lateinit var archiveRestartButton: Button

    private var lastPosition : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_archive, container, false)
        //init interface communicator for swipeRefreshLayout to refresh data from other fragment
        communicator = activity as Communicator
        refreshCom = activity as RefreshCom

        initRecyclerView(view)


        //refresh layout
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_archive)
        swipeRefreshLayout.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
                getOrdersData()
                //To refresh Data in CurrentFragment (send argument):
                refreshCom.refreshFragment(true)



            }, 1000)
        }

        archiveRestartButton.setOnClickListener { getOrdersData() }





        getOrdersData()

        return view
    }

    private fun initRecyclerView(view: View) {
        //get a reference to recyclerView
        archiveRecyclerView = view.findViewById<RecyclerView>(R.id.rvArchiveFragment)
        //set layoutManager
        layoutManager = LinearLayoutManager(activity)
        archiveRecyclerView.layoutManager = layoutManager
        //
        archiveRecyclerView.setHasFixedSize(true)
        //set progress bar
        archiveProgressBar = view.findViewById(R.id.progress_bar_archive)
        //When is need restart
        archiveRestartTextView = view.findViewById(R.id.archive_text_view_restart_no_orders)
        //When is need restart
        archiveRestartButton = view.findViewById(R.id.archive_restart_button_no_orders)

        archiveLimitRequestTextView = view.findViewById(R.id.archive_text_view_limit_request)

    }
    fun refreshData() {
        getOrdersData()
        Log.d("TESTING", "Refresh Archive")
    }
    fun removeData(pos: Int) {
        adapter.removeItem(pos)
    }



    private fun getOrdersData()  {
        //List for order of active/current
        val orderArrayListArchive: ArrayList<ActiveDataModel> = arrayListOf()

        //Use library okHttpClient to take body from JSON
        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(ConstDatabase.ORDER_URL)
            .addHeader("authorization", ConstDatabase.KEY)
            .build()

        archiveProgressBar.visibility = View.VISIBLE
        archiveRestartButton.visibility = View.GONE
        archiveRestartTextView.visibility = View.GONE
        archiveLimitRequestTextView.visibility = View.GONE

        //Function is asynch, so we have to predicted this
        okHttpClient.newCall(request).enqueue(object : Callback {
            //when will be Fail
            override fun onFailure(call: Call, e: IOException) {
                Log.d("getOrdersData", "Response: $e")
                Toast.makeText(activity, "Failure request", Toast.LENGTH_SHORT).show()
            }

            //when will be Response, function asynch!!!
            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                if(response.message.isNullOrBlank() && (!body.isNullOrBlank())) {
                    response.apply {
                        if (!response.isSuccessful)
                        {
                            activity!!.runOnUiThread{ archiveLimitRequestTextView.visibility = View.VISIBLE }

                            throw IOException("Unexpected code $response")
                        }


                        //Create intent and put here body for ItemActivity if will be open
                        val intent = Intent(context, ItemActivity::class.java)
                        intent.putExtra("body", body)

                        Log.d(javaClass.name, "Response: $body")

                        //ASSIGNING DATA
                        val gson = GsonBuilder().create()
                        val gsonMainArray = gson.fromJson(body, Array<OrderDataModel>::class.java)


                        //While which take from JSON Array of orders
                        for (i in gsonMainArray.size - 1 downTo 0) {
                            val orderObj = gsonMainArray[i]
                            //if is current order
                            if (orderObj.archive) {

                                Log.d(javaClass.name, "Order $i is active/current!")

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
                                orderArrayListArchive.add(orderDetails)
                            }
                            //for archive orders

                        }
                        //because it is asynch function and view cannot be here, we take this to new ui thread
                        activity?.runOnUiThread {
                            adapter = OrdersAdapter(orderArrayListArchive)
                            archiveRecyclerView.adapter = adapter
                            archiveProgressBar.visibility = View.GONE

                            //From lambda - OrdersAdapter
                            adapter.listener = {
                                activity!!.startActivityForResult(intent, 1)
                            }

                        }
                    }
                }
                else {
                    activity!!.runOnUiThread {
                        archiveProgressBar.visibility = View.GONE
                        archiveRestartButton.visibility = View.VISIBLE
                        archiveRestartTextView.visibility = View.VISIBLE
                    }
                }
            }


        })

    }





}