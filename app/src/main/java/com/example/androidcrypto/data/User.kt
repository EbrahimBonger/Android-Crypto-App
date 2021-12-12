package com.example.androidcrypto.data

import java.io.Serializable

data class User(
    val userId: String? = null,
    var username: String? = null,
    var accountBalance: Double = 0.0,
    var asset: MutableMap<String, Asset>? = null): Serializable{

}


