package com.example.androidcrypto.transaction

import android.app.Application
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.androidcrypto.constant.Constants.Companion.BUY
import com.example.androidcrypto.constant.Constants.Companion.INITIAL_BALANCE
import com.example.androidcrypto.constant.Constants.Companion.SELL
import com.example.androidcrypto.data.Asset
import com.example.androidcrypto.data.User
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


object FunctionUtil: Application() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User
    private lateinit var globalAsset: Asset
    private var SUFFICIENT_BALANCE: Boolean = true
    private lateinit var assets: List<Asset>



    // setting fun's
    fun writeNewUser(userId: String): Boolean {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val reference = firebaseDatabase.getReference("users")
        var accountSuccess: Boolean = true


        // initialize map for asset
        var assetMap = mutableMapOf<String, Asset>()
//        var asset: Asset = Asset("ASSET", "ASSET", 0.0, "0", "HTTP", 0.0)
//        assetMap[asset.assetId] = asset

        val user = User(userId, "name", INITIAL_BALANCE, assetMap)
        reference.child(userId).setValue(user).addOnSuccessListener {
            accountSuccess = true
        }.addOnFailureListener {
            accountSuccess = false
        }

        return accountSuccess
    }


    fun setUserBalance(userId: String, balance: Double) {
        // init database
        firebaseDatabase = FirebaseDatabase.getInstance()
        val balanceReference = firebaseDatabase.getReference("users").child(userId).child("user_balance")
        balanceReference.setValue(balance)
    }


    // getting funs
    private fun getUsername(): String {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val userIdReference = firebaseDatabase.getReference("users").child("user_name")

        return ""
    }

    private fun getUserBalance(userId: String) {
        databaseReference =
            userId?.let { it1 ->
                FirebaseDatabase.getInstance().getReference("users").child(
                    it1
                ).child("accountBalance")
            }!!
        databaseReference.get().addOnSuccessListener {
            if(it.exists()) {
                var currentBalance: Double? = it.getValue<Double>()
                if (currentBalance != null) {

                }

            }
        }
    }

    private fun getUserAsset() {}

    // write functions
    fun addAsset(userId: String, asset: Asset) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("users")

        var assetMap = mutableMapOf<String, Asset>()
        assetMap[asset.assetId] = asset
        databaseReference.child(userId).child("asset").updateChildren(assetMap as Map<String, Any>).addOnSuccessListener {

        }.addOnFailureListener {  }
    }

    // read functions
    fun readDatabase(userId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId).get().addOnSuccessListener{

            if(it.exists()){
                // it represents the node
                val userId = it.child("userId")
                val accountBalance = it.child("accountBalance")
                val username = it.child("username")
                val asset = it.child("asset")

                Log.d("userRetrieval", "userId: $userId balance: $accountBalance username: $username asset: $asset")
            }
            else {
                Log.d("userRetrieval", "NULL")
            }
        }.addOnFailureListener {

        }
    }

    // get functions
    fun getAssetById(userId: String, assetId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId).child("asset").get().addOnSuccessListener{

            if(it.exists()){
                // it represents the node
                val asset = it.child("eth")

                Log.d("ethRetrieval", "userId: $userId asset: $asset it: $it")
            }
            else {
                Log.d("ethRetrieval", "NULL")
            }
        }.addOnFailureListener {

        }

    }

    fun getAllAsset(userId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId).child("asset").get().addOnSuccessListener{

            if(it.exists()){
                // it represents the node
                val asset = it

                Log.d("Retrieval", "userId: $userId asset: $asset it: $it")
            }
            else {
                Log.d("ethRetrieval", "NULL")
            }
        }.addOnFailureListener {
            Log.d("addOnFailureListener", "addOnFailureListener")
        }

    }

    // update functions
    fun updateAsset(userId: String, asset: Asset, amount: Double) {
        // init database
        firebaseDatabase = FirebaseDatabase.getInstance()
        val assetAmountUpdateReference = firebaseDatabase.getReference("users").child(userId)
            .child("asset").child(asset.assetId).child("assetAmount")

        val balanceUpdateReference = firebaseDatabase.getReference("users").child(userId).child("userBalance")

        var asset_map= mutableMapOf<String, Asset>()
        asset_map[asset.assetId] = asset

        // start reading assetMap data from the database
        balanceUpdateReference.setValue(asset.assetUnitPrice *amount)
        assetAmountUpdateReference.setValue(amount)
        assetAmountUpdateReference.updateChildren(asset_map as Map<String, Any>)

    }

    // delete functions

    // asset transaction
    fun getAccountBalance(userId: String): Double {
        var currBalance: Double = 0.0
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("accountBalance")
        databaseReference.get().addOnSuccessListener {
            if (it.exists()){
                it.key
                var currBalanceStr: String = it.value.toString()
                currBalance = currBalanceStr.toDouble()
                Log.d("getAccountBalance", "key: ${it.key} value: ${it.value}")
            }
        }.addOnFailureListener {
            Log.d("getAccountBalance", "NULL")
        }
        Log.d("getAccountBalance", "currBalance: $currBalance")
        return currBalance
    }

    fun checkAssetExist(userId: String, assetId: String): Boolean {
        var isExist: Boolean = false
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("asset").child(assetId)
        databaseReference.get().addOnSuccessListener {
            if (it.exists()){
                isExist = true
                it.key
                it.value
                Log.d("checkAssetExist", "key: ${it.key} value: ${it.value}")
            }
            else{
                Log.d("checkAssetExist", "Does NOT exist")
            }
        }.addOnFailureListener {
            Log.d("checkAssetExist", "NULL")
        }
        return isExist
    }

    fun buyAsset(userId: String, asset: Asset, amount: Double): Boolean{


        val assetId: String = asset.assetId
        val unitPrice: Double = asset.assetUnitPrice

        Log.d("buyAsset", "Buy Asset Called asset:${unitPrice} : ${assetId}")

        val buyingCost: Double = calculateCost(unitPrice, amount)


        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("accountBalance")
        databaseReference.get().addOnSuccessListener {
            if (it.exists()){
                it.key
                var currentBalance: Double? = it.getValue<Double>()
                Log.d("buyAsset", "cost: ${buyingCost} value: ${it.value}")

                if (currentBalance != null) {
                    if(currentBalance < buyingCost) {
                        SUFFICIENT_BALANCE = false
                        Log.d("buyAsset", "insufficient fund currBalance: ${currentBalance} cost: $buyingCost requested amount: $SUFFICIENT_BALANCE")
                    } else {
                        // update user balance

                        // check if the user has the asset
                        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("asset").child(assetId)
                        databaseReference.get().addOnSuccessListener {
                            if (it.exists()){
                                updateUserAccount(userId, asset.assetId, amount, buyingCost, BUY)
                                Log.d("checkAssetExist", "key: ${it.key} value: ${it.value} assetId: $assetId")
                            } else{
                                asset.assetAmount = amount
                                updateBalance(userId, -buyingCost)
                                addAsset(userId, asset)

                                Log.d("buyAsset", "buyAsset init asset: $asset")
                            }
                        }.addOnFailureListener {
                            Log.d("checkAssetExist", "NULL")
                        }.addOnFailureListener{
                            asset.assetAmount = amount
                            addAsset(assetId, asset)
                        }

                    }
                }
            }
        }.addOnFailureListener {
            Log.d("getAccountBalance", "NULL")
        }

        Log.d("SUFFICIENT_BALANCE", "SUFFICIENT_BALANCE: $SUFFICIENT_BALANCE")
        return SUFFICIENT_BALANCE
    }

    fun sellAsset(userId: String, asset: Asset, amount: Double): Boolean{

        Log.d("sellAsset", "Sell Asset Called asset: ${asset.assetId}")
        val assetId: String = asset.assetId
        val unitPrice: Double = asset.assetUnitPrice

        val sellingCost: Double = calculateCost(unitPrice, amount)


        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("accountBalance")
        databaseReference.get().addOnSuccessListener {
            if (it.exists()){
                it.key
                var currentBalance: Double? = it.getValue<Double>()
                Log.d("sellAsset", "cost: ${sellingCost} value: ${it.value}")

                if (currentBalance != null) {

                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("asset").child(assetId)
                    databaseReference.get().addOnSuccessListener {
                        if (it.exists()){
                            updateUserAccount(userId, asset.assetId, amount, sellingCost, SELL)
                            Log.d("checkAssetExist", "key: ${it.key} value: ${it.value} assetId: $assetId")
                        } else{

                            Log.d("sellAsset", "sellAsset init asset: $asset")
                        }
                    }

                }
            }
        }.addOnFailureListener {
            Log.d("getAccountBalance", "NULL")
        }

        Log.d("SUFFICIENT_BALANCE", "SUFFICIENT_BALANCE: $SUFFICIENT_BALANCE")
        return SUFFICIENT_BALANCE
    }

    private fun calculateCost(unitPrice: Double, amount: Double): Double{
        var result = unitPrice*amount
        Log.d("calculateCost", "calculateCost: $result ")
        return result
    }

    private fun updateUserAccount(userId: String, assetId: String, amount: Double, updatedCost: Double, flag: String) {
        var targetedPath: String = "assetAmount"
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId).child("asset").child(assetId).child(targetedPath).get().addOnSuccessListener {

            if(it.exists()) {

                var previousAmountDbl: Double? = it.getValue<Double>()

                if(flag == BUY) {
                    updateBalance(userId, -updatedCost)

                    databaseReference.child(userId).child("accountBalance").get()

                    var newAmount: Double = (previousAmountDbl?.plus(amount) ?: Double) as Double

                    databaseReference.child(userId).child("asset").child(assetId).child(targetedPath).setValue(newAmount)


                }else if(flag == SELL) {

                    updateBalance(userId, updatedCost)

                    databaseReference.child(userId).child("accountBalance").get()

                    var newAmount: Double = (previousAmountDbl?.minus(amount) ?: Double) as Double

                    if(newAmount <= 0){
                        Log.d("updateUserAccount", "amount: $amount")
                        databaseReference.child(userId).child("asset").child(assetId).removeValue()
                    }else{
                        databaseReference.child(userId).child("asset").child(assetId).child(targetedPath).setValue(newAmount)
                    }

                }
                else{
                    // error
                }

            }
        }
    }

    private fun updateBalance(userId: String, updatedCost: Double) {
        Log.d("updateBalance", "updateBalance init: $updatedCost")
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId).child("accountBalance").get().addOnSuccessListener {
            if(it.exists()){
                var previousBalance: Double? = it.getValue<Double>()
                var newBalance: Double = (previousBalance?.plus(updatedCost) ?: Double) as Double

                databaseReference.child(userId).child("accountBalance").setValue(newBalance)

            }
        }.addOnFailureListener {

        }
    }

    fun portfolioSnapshot(userId: String): List<Asset> {


        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.child(userId).get().addOnSuccessListener {
            if(it.exists()){
                // it has user info
                Log.d("portfolioSnapshot", "portfolio: ${it}")
            }
        }.addOnFailureListener{
            Log.d("portfolioSnapshot", "NULL")
        }
        return assets
    }

    fun showProgressBarLoading(progressBar: ProgressBar) {
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED))
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBarLoading(progressBar: ProgressBar) {
        progressBar.visibility = View.INVISIBLE
    }

}