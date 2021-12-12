package com.example.androidcrypto.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidcrypto.R
import com.example.androidcrypto.activity.Wallet
import com.example.androidcrypto.data.Asset
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.math.RoundingMode


class WalletAdapter(private val assets: List<Asset>,
                    val clickListener: Wallet) : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {



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
        viewHolder.crypto_amount.text = currCrypto.assetAmount.toString()
        viewHolder.crypto_volume.text = "$${currCrypto.assetTotalValue.toString()}"


        if (currCrypto.asset_urlToImage.isNotBlank()) {
            Picasso.get().setIndicatorsEnabled(true)

            Picasso
                .get()
                .load(currCrypto.asset_urlToImage)
                .into(viewHolder.icon)
        }



        viewHolder.init(assets.get(position), clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val crypto_name: TextView = itemView.findViewById(R.id.crypto_name)
        val crypto_amount: TextView = itemView.findViewById(R.id.crypto_price) // for this case is amount
        val crypto_volume: TextView = itemView.findViewById(R.id.crypto_volume)
        val icon: ImageView = itemView.findViewById(R.id.imageView)




        fun init(item: Asset, action: Wallet) {
            val assetAmount_big_decimal: BigDecimal = item.assetAmount.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
            val assetTotalValue_big_decimal: BigDecimal = item.assetTotalValue.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)

            crypto_name.text = item.assetName
            crypto_amount.text = assetAmount_big_decimal.toString()
            crypto_volume.text = "$${assetTotalValue_big_decimal.toString()}"

            itemView.setOnClickListener {
                action.onItemClick(item, absoluteAdapterPosition)
            }


        }

    }


}

interface OnPortifolioClickListener {
    fun onItemClick(item: Asset, position: Int) {

    }
}

