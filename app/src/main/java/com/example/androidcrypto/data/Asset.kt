package com.example.androidcrypto.data

import java.io.Serializable


data class Asset(

    val assetId: String,
    val assetName: String,
    var assetUnitPrice: Double,
    val assetVolume: String,
    val asset_urlToImage: String,
    var assetAmount: Double = 0.0,
    val assetTotalValue: Double = 0.0,
): Serializable