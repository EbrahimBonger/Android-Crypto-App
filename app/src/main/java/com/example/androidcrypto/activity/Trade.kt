package com.example.androidcrypto.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.androidcrypto.R
import com.example.androidcrypto.function.Constants.Companion.BUY
import com.example.androidcrypto.function.Constants.Companion.SELL
import com.example.androidcrypto.data.Asset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import java.lang.Thread.sleep

class Trade : AppCompatActivity()  {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var asset_name: TextView
    private lateinit var asset_unit_price: TextView
    private lateinit var buy_popup_btn: Button
    private lateinit var sell_popup_btn: Button
    private lateinit var back_to_dashboard: Button
    private lateinit var transcation_type_btn: Button
    private lateinit var portfolio_btn: Button
    private lateinit var transcation_type: String
    private lateinit var user: FirebaseAuth
    private lateinit var signout_btn: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)


//        showProgressBarLoading(progressBar)


        user = FirebaseAuth.getInstance()
        if(user.currentUser != null) {
            user.currentUser?.let{

            }
        }

        title = getString(R.string.trade_title)


        var asset = intent.getSerializableExtra("ASSET")  as? Asset
        var USER_UID = intent.getStringExtra("USER_UID")

        // this setting call uses when the navigation called from dashboard activity to trade activity
        asset_name = findViewById(R.id.asset_title)
        asset_name.setText(asset?.assetName)

        asset_unit_price = findViewById(R.id.asset_unit_price)
        if (asset != null) {
            asset_unit_price.setText("$${asset.assetUnitPrice}")
        }
//        hideProgressBarLoading(progressBar)


        // go to portfolio page
        portfolio_btn = findViewById(R.id.wallet_btn)

        portfolio_btn.setOnClickListener {
            val intent: Intent = Intent(this, Wallet::class.java)
            intent.putExtra("USER_UID", USER_UID)
            startActivity(intent)
        }

        // the db call uses for to retrieve asset data
        // when the navigation called from wallet activity to trade activity
        firebaseDatabase = FirebaseDatabase.getInstance()

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        if (USER_UID != null) {
            if (asset != null) {
                databaseReference.child(USER_UID).child("asset").child(asset.assetId).get().addOnSuccessListener {
                    if(it.exists()) {
                        val assetUnitPrice: Double? = it.child("assetUnitPrice").getValue<Double>()
                        if (assetUnitPrice != null) {
                            asset.assetUnitPrice = assetUnitPrice
                        }

                        asset_name = findViewById(R.id.asset_title)
                        asset_name.setText(asset?.assetName)

                        asset_unit_price = findViewById(R.id.asset_unit_price)
                        asset_unit_price.setText("$${assetUnitPrice}")

                    }
                }
            }
        }


        buy_popup_btn = findViewById(R.id.buy_popup_btn)
        sell_popup_btn = findViewById(R.id.sell_popup_btn)
        back_to_dashboard = findViewById(R.id.back_to_dashboard)

        buy_popup_btn.setOnClickListener {

            var dialog = asset?.let { it1 -> PopupTradeDialog(USER_UID, BUY, it1) }
            if (dialog != null) {
                dialog.show(supportFragmentManager, "Buy Transaction")
            }
        }

        sell_popup_btn.setOnClickListener {

            var dialog = asset?.let { it1 -> PopupTradeDialog(USER_UID, SELL, it1) }
            if (dialog != null) {
                dialog.show(supportFragmentManager, "Sell Transaction")
            }
        }

        back_to_dashboard.setOnClickListener {
            val intent: Intent = Intent(this, Dashboard::class.java)
            intent.putExtra("USER_UID", USER_UID)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.signout_btn){
            user.signOut()
            startActivity(
                Intent(this, LogIn::class.java
                )
            )
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setTransactionType(type: String) {
        var rootView: View = layoutInflater.inflate(R.layout.fragment_trade_popup_dialog, null)

        transcation_type_btn = rootView.findViewById(R.id.transcation_type)
        transcation_type_btn.setText(type)
    }


}

