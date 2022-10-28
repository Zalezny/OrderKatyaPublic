package com.example.katya.orders.recyclers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderappkatya.ConstDatabase
import com.example.orderappkatya.R
import com.example.orderappkatya.datamodels.ActiveDataModel
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class OrdersAdapter(private val ordersList: ArrayList<ActiveDataModel>) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    //init lambda of Callback
    var listener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        Log.d("ActiveAdapter", "onCreateViewHolder is opened")
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_item,
            parent, false
        )
        return OrdersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        Log.d("ActiveAdapter", "onBindViewHolder is opened")
        val currentItem = ordersList[position]
        holder.numberOfOrder.text = "Order nr ${currentItem.numberOfOrder.toString()}"
        holder.nameOfCustomer.text = currentItem.nameOfCustomer
        holder.dateEx.text = currentItem.dateEx!!.dropLast(14)
        holder.priceEx.text = currentItem.priceEx.toString()
        holder.deliveryEx.text = getDeliveryName(currentItem.deliveryEx)
        holder.phoneEx.text = currentItem.phoneEx

        val isNew = currentItem.isNew
        val id = currentItem.id

        if (isNew)
            holder.ivNew.visibility = View.VISIBLE
        else
            holder.ivNew.visibility = View.GONE


        val isVisible: Boolean = currentItem.visibility
        holder.expandableLayout.visibility = if (isVisible) View.VISIBLE else View.GONE

        //set ivArrow
        if (isVisible) {
            holder.ivArrow.setImageResource(R.drawable.ic_arrow_down)
        } else {
            holder.ivArrow.setImageResource(R.drawable.ic_arrow_forward)
        }

        holder.arrowItem.setOnClickListener {
            currentItem.visibility = !currentItem.visibility
            notifyItemChanged(position)
        }

        holder.linearLayoutItem.setOnClickListener { v ->
            val ivNewVisibility = holder.ivNew.visibility
            if (ivNewVisibility == View.VISIBLE) {
                //set visibility of icon new order
                holder.ivNew.visibility = View.GONE
                //send to database information about change
                patchDataIsNew(id!!, false)
            }
            //intent for ItemActivity (from Activity)
            currentItem.intent?.putExtra("NumberOrder", currentItem.numberOfOrder)
            currentItem.intent?.putExtra("position", position)
            currentItem.intent?.putExtra("body", currentItem.id)


            //callback to ArchiveFragment/CurrentFragment
            listener?.invoke()


        }


    }


    private fun getDeliveryName(deliveryName: String?): CharSequence? {
        if (deliveryName == "Odbiór osobisty") return "osobiście"
        else return deliveryName
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }


    class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val numberOfOrder: TextView = itemView.findViewById(R.id.tvNumberOfOrder)
        val nameOfCustomer: TextView = itemView.findViewById(R.id.tvNameOfCustomer)
        val dateEx: TextView = itemView.findViewById(R.id.tvDateEx)
        val priceEx: TextView = itemView.findViewById(R.id.tvPriceEx)
        val deliveryEx: TextView = itemView.findViewById(R.id.tvDeliverEx)
        val phoneEx: TextView = itemView.findViewById(R.id.tvPhoneEx)
        val expandableLayout: LinearLayout = itemView.findViewById(R.id.expanded_layout)
        val arrowItem: ImageView = itemView.findViewById(R.id.arrow_item)

        val linearLayoutItem: LinearLayout = itemView.findViewById(R.id.ll_item)

        val ivArrow: ImageView = itemView.findViewById(R.id.arrow_item)

        val ivNew: ImageView = itemView.findViewById(R.id.iv_new_interjection_order_item)

    }

    fun removeItem(pos: Int) {
        ordersList.removeAt(pos)
        notifyDataSetChanged()
    }

    private fun patchDataIsNew(id: String, amount: Boolean) {
        val dynamicUrl = "${ConstDatabase.ORDER_URL}$id"
        val json: JsonObject = JsonObject()
        json.addProperty("newOrder", amount)

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(dynamicUrl)
            .patch(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("authorization", ConstDatabase.KEY)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("FailureResponse", "$e")
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful)
                Log.d(javaClass.name, "Response success")

            }
        })
    }



}

