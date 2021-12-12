package com.example.androidcrypto.firebase

import com.example.androidcrypto.data.Asset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class RealDatabaseFetch {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth


    public fun setUserBalance(balance: Double) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val balanceReference = firebaseDatabase.getReference("users").child(getUserId()).child("user_balance")
        balanceReference.setValue(balance)
    }

    private fun setUserAsset(userId: String, asset: Asset) {

    }

    private fun getUserId() : String {
        val currentUser: FirebaseUser = firebaseAuth.currentUser!!
        return currentUser.uid
    }

    private fun getUsername(): String {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val userIdReference = firebaseDatabase.getReference("users").child("user_name")

        return ""
    }

    private fun getUserBalance() {}

    private fun getUserAsset() {}

}