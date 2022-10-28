package com.example.orderappkatya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderappkatya.databinding.ActivityItemBinding
import com.example.orderappkatya.recyclers.CartAdapter
import com.example.orderappkatya.datamodels.CartDataModel
import com.example.orderappkatya.datamodels.OrderDataModel
import com.fasterxml.jackson.databind.util.ClassUtil.name
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass

class ItemActivity : AppCompatActivity() {

    private lateinit var binding : ActivityItemBinding

    private var id : String = ""
    private var position : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //show on action bar the back arrow
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        //set layoutManager
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        //
        binding.cartRecyclerView.setHasFixedSize(true)

        //Take data from previous activities
        val bundle: Bundle ?= intent.extras
        // it's id which app's got from firebase (its new id
        var mainID = bundle!!.getString("body")
        //issue from database with '"' so mainID have to filtering it.
        mainID = mainID!!.replace("\"","")
        position = bundle.getInt("position")
        val dynamicUrl = ConstDatabase.ORDER_URL + mainID


        getIdData(dynamicUrl)



        binding.btnArchive.setOnClickListener {
            patchToServer(true, dynamicUrl)
            backData(true, position)
            finish()
        }

        binding.btnUnarchive.setOnClickListener {
            patchToServer(false, dynamicUrl)
            backData(false,position)
            finish()
        }


    }

    //to return arrow back
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //back Data to MainActivity (need it dynamic RV)
    private fun backData(isArchive: Boolean, order: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra("isArchive", isArchive)
        returnIntent.putExtra("numberOfOrder", order)
        returnIntent.putExtra("position", position)

        setResult(RESULT_OK, returnIntent)
    }

    private fun patchToServer(amount: Boolean, dynamicUrl: String) {
        val json: JsonObject = JsonObject()
        json.addProperty("archive", amount)

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .patch(json.toString().toRequestBody("application/json".toMediaType()))
            .url(dynamicUrl)
            .addHeader("authorization", ConstDatabase.KEY)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(javaClass.name, "$e")
            }

            override fun onResponse(call: Call, response: Response) {

                var archiveString: String = ""
                if(amount) archiveString = "Archive"
                else archiveString = "Unarchive"

                runOnUiThread {
                    if (response.isSuccessful)
                        Toast.makeText(
                            this@ItemActivity,
                            "$archiveString is Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                }

            }
        })
    }

    private fun getIdData(dynamicUrl: String)  {

        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
            .url(dynamicUrl)
            .addHeader("authorization", ConstDatabase.KEY)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(javaClass.name, "Response: $e")
                Toast.makeText(baseContext, "Failure request", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()

                if(response.message.isNullOrBlank() ) {
                    response.apply {
                        if(!response.isSuccessful) throw IOException("Unexpected code $response")

                        runOnUiThread {

                            //gson - change body in objects/arrays
                            val gson = GsonBuilder().create()
                            val feed = gson.fromJson(body, OrderDataModel::class.java)

                            val nr = feed.orderNumber
                            //set text views in activity (without recycler view)
                            binding.tvPriceForDelivery.text = feed.shipment.method["price"].toString() + " zł"
                            binding.tvDelivery.text = feed.shipment.method["kind"].toString()

                            binding.tvFullName.text = feed.name + " " + feed.lastName
                            binding.tvStreetAndNumber.text = feed.street
                            binding.tvCityAndPostCode.text = feed.postCode + " " + feed.city
                            binding.tvPhoneNumber.text = feed.phone
                            binding.tvEmail.text = feed.email

                            //set BOX MACHINE (paczkomat)
                            val nameMachine = feed.shipment.point["name"]
                            val addressMachine = feed.shipment.point["address"]
                            val descriptionMachine = feed.shipment.point["description"]

                            if(!nameMachine.isNullOrBlank() && !addressMachine.isNullOrBlank())
                            {
                                //show of elements of box machine
                                binding.tvBoxMachine.visibility = View.VISIBLE
                                binding.tvNameMachine.visibility = View.VISIBLE
                                binding.tvAddressMachine.visibility = View.VISIBLE

                                //set text of machine box
                                binding.tvNameMachine.text = nameMachine
                                binding.tvAddressMachine.text = addressMachine
                                //if descriptionMachine exist (is not empty)
                                if(!descriptionMachine.isNullOrBlank())
                                {
                                    binding.tvDescriptionMachine.visibility = View.VISIBLE
                                    binding.tvDescriptionMachine.text = descriptionMachine
                                }

                            }

                            //Date of bought formatting and setting
                            val dateString = feed.date
                            val sdfdate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(dateString)
                            val formatedDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(sdfdate!!)

                            binding.tvDate.text = formatedDate.toString()

                            //Comments to order/s
                            binding.tvCommentary.text = if(feed.comments != "") feed.comments else "brak komentarza"


                            //Summary Price
                            var summary = 0.0F
                            summary += feed.shipment.method["price"]!!.toFloat()
                            for(j in 0 until feed.order.size)
                            {
                                var price = feed.order[j].price
                                var amount = feed.order[j].quantity
                                summary += price*amount
                            }
                            //Check the rest of float and convert on chosen formats
                            val summaryString: String
                            if (summary % 1L > 0L) summaryString = DecimalFormat("#.##").format(summary)
                            else summaryString = DecimalFormat("#").format(summary)
                            binding.tvSummaryPrice.text = summaryString + " zł"

                            //CartDataModel
                            val cart = feed.order
                            var cartArrayList: ArrayList<CartDataModel> = arrayListOf()
                            for(k in cart.indices)
                            {
                                val title = cart[k].title
                                val color = cart[k].color
                                val photo = cart[k].photo
                                val quantity = cart[k].quantity
                                val price = cart[k].price
                                val size : String? = cart[k].size

                                var cartData = CartDataModel(title, color, photo, quantity, price, size)

                                cartArrayList.add(cartData)
                            }
                            //Create adapter with cartArrayList
                            binding.cartRecyclerView.adapter = CartAdapter(cartArrayList)

                            //ARCHIVE ALLOW CORRECT BUTTON
                            val isArchive = feed.archive
                            if(isArchive) binding.btnUnarchive.visibility = View.VISIBLE
                            else binding.btnArchive.visibility = View.VISIBLE

                            //DYNAMIC URL TAKE ID
                            id = feed._id






                            //Set TV Order Number
                            binding.tvItemOrderNumber.text = "Zamówienie nr $nr"


                        }
                    }
                }
            }



        })


    }



}