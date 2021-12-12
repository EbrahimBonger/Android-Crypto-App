package com.example.androidcrypto.adapter

import android.content.Context
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidcrypto.R
import com.example.androidcrypto.activity.Dashboard
import com.example.androidcrypto.data.Asset
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import kotlin.math.roundToLong


class DashboardAdapter(private val assets: List<Asset>,
                       val clickListener: Dashboard) : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {



    override fun getItemCount(): Int {
        return assets.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val cryptoListItemView: View = layoutInflater.inflate(R.layout.crypto_item, parent, false)

        // We can now create a ViewHolder from the root view
        return ViewHolder(cryptoListItemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val currCrypto = assets[position]
        viewHolder.crypto_name.text = currCrypto.assetName
        viewHolder.crypto_price.text = currCrypto.assetUnitPrice.toString()
        viewHolder.crypto_volume.text = currCrypto.assetVolume


        if (currCrypto.asset_urlToImage.isNotBlank()) {
            Picasso.get().setIndicatorsEnabled(true)
            Picasso
                .get()
                .load(currCrypto.asset_urlToImage)
                .into(viewHolder.icon)
        }


        viewHolder.initi(assets.get(position), clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val crypto_name: TextView = itemView.findViewById(R.id.crypto_name)
        val crypto_price: TextView = itemView.findViewById(R.id.crypto_price)
        val crypto_volume: TextView = itemView.findViewById(R.id.crypto_volume)
        val icon: ImageView = itemView.findViewById(R.id.imageView)




        fun initi(item: Asset, action: Dashboard) {
            crypto_name.text = item.assetName
            crypto_price.text = item.assetUnitPrice.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toString()
            crypto_volume.text = item.assetVolume


            itemView.setOnClickListener {
                action.onItemClick(item, absoluteAdapterPosition)
            }


        }

    }


}

interface OnCryptoClickListener {
    fun onItemClick(item: Asset, position: Int) {

    }
}