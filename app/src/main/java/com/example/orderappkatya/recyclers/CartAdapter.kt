package com.example.orderappkatya.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderappkatya.datamodels.CartDataModel

import android.widget.ImageView
import com.example.orderappkatya.R
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class CartAdapter(private val cartList : ArrayList<CartDataModel>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cart_item,
            parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartList[position]
        holder.tvTitle.text = currentItem.title
        if(currentItem.color != null) {
            if (currentItem.color.isNotEmpty())
                holder.tvColor.text = "kolor: " + currentItem.color.toString()
            else {
                holder.tvColor.visibility = View.GONE
            }
        } else {
            holder.tvColor.visibility = View.GONE
        }
        holder.tvQuantity.text = "ilość: " + currentItem.quantity.toString()

        if(currentItem.size != null) {
            holder.tvColor.text = "size: " + currentItem.size.toString()
            holder.tvColor.visibility = View.VISIBLE
        }
        //Format a rest price
        val priceString: String
        if (currentItem.price % 1L > 0L) priceString = DecimalFormat("#.##").format(currentItem.price)
        else priceString = DecimalFormat("#").format(currentItem.price)
        holder.tvPrice.text = priceString + " zł"

        //IMAGE VIEW
        val imageUrl = "https://" + currentItem.photoUrl
        Picasso.get().load(imageUrl).into(holder.ivPhoto)

    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle : TextView = itemView.findViewById(R.id.title_cart_item)
        val tvColor : TextView = itemView.findViewById(R.id.color_cart_item)
        val ivPhoto : ImageView = itemView.findViewById(R.id.image_cart_item)
        val tvQuantity : TextView = itemView.findViewById(R.id.quantinty_cart_item)
        val tvPrice : TextView = itemView.findViewById(R.id.price_cart_item)


    }

}