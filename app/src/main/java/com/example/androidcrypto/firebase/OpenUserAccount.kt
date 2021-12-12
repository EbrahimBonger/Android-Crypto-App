package com.example.androidcrypto.firebase

import com.google.firebase.database.FirebaseDatabase

class OpenUserAccount {

    private lateinit var firebaseDatabase: FirebaseDatabase

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()

    }

}