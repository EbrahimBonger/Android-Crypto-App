package com.example.androidcrypto.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.androidcrypto.R
import com.example.androidcrypto.constant.Constants.Companion.BUY
import com.example.androidcrypto.constant.Constants.Companion.SELL
import com.example.androidcrypto.transaction.FunctionUtil.buyAsset
import com.example.androidcrypto.transaction.FunctionUtil.sellAsset
import com.example.androidcrypto.data.Asset
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import org.jetbrains.anko.colorAttr

class PopupTradeDialog(USER_UID: String?, transactionType: String, asset: Asset): DialogFragment() {

    private lateinit var transcation_type_btn: Button
    private lateinit var transaction_cancel: Button
    private lateinit var transactionProgressBar: ProgressBar
    private lateinit var inputted_amount: EditText

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var USER_UID = USER_UID
    private var transactionType = transactionType
    private var asset = asset


    @SuppressLint("StringFormatInvalid")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(R.layout.fragment_trade_popup_dialog, container, false)

        transactionProgressBar = rootView.findViewById(R.id.transactionProgressBar)
        transcation_type_btn = rootView.findViewById(R.id.transcation_type)
        transaction_cancel = rootView.findViewById(R.id.transaction_cancel)
        transcation_type_btn.isEnabled = false


        if(transactionType == BUY){
            transcation_type_btn.setText(R.string.buy_popup_btn)
        }
        else {
            transcation_type_btn.setText(R.string.sell_popup_btn)
        }





        transcation_type_btn.isEnabled = false

        transcation_type_btn.setOnClickListener{
            // Update the  db
            inputted_amount = rootView.findViewById(R.id.inputted_amount)
            val amount: Double = inputted_amount.getText().toString().toDouble()


            if(transactionType == BUY) {
                activity?.let { it1 ->
                    ContextCompat.getColor(
                        it1, R.color.Green)
                }?.let { it2 -> transcation_type_btn.setBackgroundColor(it2) }
                // before buying asset, check if the user has sufficient fund
                databaseReference =
                    USER_UID?.let { it1 ->
                        FirebaseDatabase.getInstance().getReference("users").child(
                            it1
                        ).child("accountBalance")
                    }!!
                databaseReference.get().addOnSuccessListener {
                    if(it.exists()){
                        var currentBalance: Double? = it.getValue<Double>()
                        if (currentBalance != null) {
                            var requestedAmount: Double = amount * asset.assetUnitPrice
                            if(currentBalance < requestedAmount){
                                var offeset: Double = requestedAmount-currentBalance
                                Toast.makeText(activity, getString(R.string.insufficient_fund, "${offeset.toString()}"), Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            else {
                                // you can buy
                                USER_UID?.let { it1 -> buyAsset(it1, asset, amount)}
                                Toast.makeText(activity, getString(R.string.buy_success, "${asset.assetId.toString()} ${amount.toString()}"), Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        }

                    }
                }
//            sleep(100000)
//                Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
//                dismiss()
            }else if(transactionType == SELL) {
                activity?.let { it1 ->
                    ContextCompat.getColor(
                        it1, R.color.Red)
                }?.let { it2 -> transcation_type_btn.setBackgroundColor(it2) }
                // if the asset exist in the asset tree
                databaseReference =
                    USER_UID?.let { it1 ->
                        FirebaseDatabase.getInstance().getReference("users").child(
                            it1
                        ).child("asset").child(asset.assetId)
                    }!!
                databaseReference.get().addOnSuccessListener {
                    if(!it.exists()){
                        Toast.makeText(activity, getString(R.string.you_did_not_own, "${asset.assetName}!"), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }

                }


                databaseReference =
                    USER_UID?.let { it1 ->
                        FirebaseDatabase.getInstance().getReference("users").child(
                            it1
                        ).child("asset").child(asset.assetId).child("assetAmount")
                    }!!
                databaseReference.get().addOnSuccessListener {
                    if(it.exists()){
                        var currentAmount: Double? = it.getValue<Double>()
                        if (currentAmount != null) {
                            if(currentAmount < amount){
                                var offeset: Double = amount-currentAmount
                                Toast.makeText(activity, getString(R.string.insufficient_fund, "${offeset.toString()}"), Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            else {
                                // you can sell
                                USER_UID?.let { it1 -> sellAsset(it1, asset, amount)}
                                Toast.makeText(activity, getString(R.string.sell_success, "${asset.assetId.toString()} ${amount.toString()}"), Toast.LENGTH_SHORT).show()
                                dismiss()

                            }
                        }

                    }
                }.addOnFailureListener {

                    Toast.makeText(activity, "You did not own ${asset.assetName} asset!", Toast.LENGTH_SHORT).show()
                }

            }
        }


        transaction_cancel.setOnClickListener{
            dismiss()
        }


        inputted_amount = rootView.findViewById(R.id.inputted_amount) as EditText
        inputted_amount.addTextChangedListener {
            val inputted_amount: String = inputted_amount.text.toString()
            //        val inputtedPassword: String = password.text.toString()
            val enableButton: Boolean = inputted_amount.isNotBlank()

            // Kotlin shorthand for login.setEnabled(enableButton)
            transcation_type_btn.isEnabled = enableButton
        }
        transcation_type_btn.addTextChangedListener(textWatcher)
        return rootView
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Kotlin shorthand for username.getText().toString()
            // .toString() is needed because getText() returns an Editable (basically a char array).
            val inputted_amount: String = inputted_amount.text.toString()
            val enableButton: Boolean = inputted_amount.isNotBlank()

            // Kotlin shorthand for login.setEnabled(enableButton)
            transcation_type_btn.isEnabled = enableButton
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    private fun showLoading() {
        transactionProgressBar.visibility = View.VISIBLE
        transcation_type_btn.isEnabled = false
        transaction_cancel.isEnabled = false
    }

    // Hides the loading indicator and enables user input
    private fun hideLoading() {
        transactionProgressBar.visibility = View.INVISIBLE
        transcation_type_btn.isEnabled = true
        transaction_cancel.isEnabled = true
    }

}