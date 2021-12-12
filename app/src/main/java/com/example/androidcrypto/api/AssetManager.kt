package com.example.androidcrypto.api

import android.util.Log
import com.example.androidcrypto.data.Asset
import okhttp3.Request
import okhttp3.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import java.math.BigDecimal
import java.math.RoundingMode


class AssetManager {

    val okHttpClient: OkHttpClient

    init {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        okHttpClientBuilder.addInterceptor(loggingInterceptor)

        okHttpClient = okHttpClientBuilder.build()
    }

    fun retrieveTopHeadlinesNews(key: String):  List<Asset> {

        val asset_list: MutableList<Asset> = mutableListOf()


        val interval_begin: String = "1h" // 1h,1d
        val interval_end: String = "1d" // 1h,1d
        val convert: String = "USD" // USD
        val per_page: String = "100" //
        val page: String  = "1" // 1

        val request: Request = Request.Builder()
            .url("https://api.nomics.com/v1/currencies/ticker?key=$key&interval=$interval_begin,$interval_end&convert=$convert&per-page=$per_page&page=$page")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        if(response.isSuccessful && !responseBody.isNullOrBlank()) {

            /*
            how to parse json body that return without body name
            https://stackoverflow.com/questions/10164741/get-jsonarray-without-array-name
             */

            val json: JSONArray = JSONArray(responseBody)

            for (i in 0 until json.length()) {

                val values = json.getJSONObject(i)

                val crypto_id = values.getString("id")
                val crypto_name = values.getString("name")
                val crypto_price_string = values.getString("price")

                val crypto_price_big_decimal: BigDecimal = crypto_price_string.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                val crypto_price: Double = crypto_price_big_decimal.toDouble()
                val crypto_volume = values.getString("market_cap")
                val urlToImage = values.getString("logo_url")


                asset_list.add(Asset(
                    crypto_id,
                    crypto_name,
                    crypto_price,
                    crypto_volume,
                    urlToImage,
                    0.0,
                    0.0
                ))

            }
        }
        return asset_list
    }
}