package com.example.androidcrypto.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidcrypto.R
import com.example.androidcrypto.adapter.WalletAdapter
import com.example.androidcrypto.adapter.OnPortifolioClickListener
import com.example.androidcrypto.data.Asset
import com.example.androidcrypto.transaction.FunctionUtil.showProgressBarLoading
import com.example.androidcrypto.transaction.FunctionUtil.hideProgressBarLoading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlin.math.roundToLong

class Wallet : AppCompatActivity(), OnPortifolioClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var USER_UID: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var back_to_dashboard: Button
//    private lateinit var assets: List<Asset>
    private val assets: MutableList<Asset> = mutableListOf()
    private lateinit var user: FirebaseAuth
    private lateinit var signout_btn: Button
    private lateinit var progressBar: ProgressBar

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        progressBar = findViewById(R.id.walletProgressBar)

        showProgressBarLoading(progressBar)

        user = FirebaseAuth.getInstance()
        if(user.currentUser != null) {
            user.currentUser?.let{

            }
        }

        title = getString(R.string.wallet_title)

        USER_UID = intent.getStringExtra("USER_UID").toString()

        back_to_dashboard = findViewById(R.id.back_to_dashboard)

        var HTTP: String = "https://s3.us-east-2.amazonaws.com/nomics-api/static/images/currencies/MINA.png"
        var USD_image: String = "https://www.google.com/search?q=Dollar+sign+svg+link&sxsrf=AOaemvKv7t46PQZ-leZFQNtSslL2a8LR4Q:1637618206685&tbm=isch&source=iu&ictx=1&fir=3owrVvPYA5cElM%252CdbhFl9YvSZ6unM%252C_%253BN3iH7dJAJxJExM%252CPxsnc1L8bEYn0M%252C_%253BZ0Xye2rXiup0AM%252C2XIb0bnEMFZKMM%252C_%253BpjJPF9GJgzW6mM%252CPEb2Bc9LUW9VZM%252C_%253BldBRzkO4UcXXYM%252C8QJVP4DeaIJNYM%252C_%253BXKcUeAVyFp5aMM%252C80UZLJjvvz_MuM%252C_%253BtkJC_KtzXN4CEM%252CrU8KSfcW4TOwIM%252C_%253BTd0x8c0q64NbhM%252C7bKU2ERz1pPqiM%252C_%253BjNlvHJYyqArc_M%252CPEb2Bc9LUW9VZM%252C_%253ByO-uMZTKAybwMM%252C4NRRGdzoozTPEM%252C_&vet=1&usg=AI4_-kQNe_e_ba_LH61Bm6flEEQShKISHw&sa=X&ved=2ahUKEwigpfKb-6z0AhUFrHIEHYlgD9EQ9QF6BAgYEAE#imgrc=Z0Xye2rXiup0AM"

        recyclerView = findViewById(R.id.portfolio_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference =
            USER_UID?.let { it1 ->
                FirebaseDatabase.getInstance().getReference("users").child(
                    it1
                )
            }!!
        databaseReference.get().addOnSuccessListener {
            if(it.exists()) {
//                var currentBalance: Double? = it.getValue<Double>()
                var itAccountBalance = it.child("accountBalance")

                var currentBalance: Double? = itAccountBalance.getValue<Double>()?.roundToLong()
                    ?.toDouble()


                currentBalance?.let { it1 ->
                    Asset(
                        "USD", "Dollar", it1, "volume", USD_image,
                        it1,
                        it1
                    )
                }?.let { it2 -> assets.add(it2) }


                // display USD balance at the top in wallet acitvity

                var itAsset = it.child("asset").children.forEach{


                    var assetId: String? = it.child("assetId").getValue<String>()
                    var assetName: String? = it.child("assetName").getValue<String>()
                    var assetAmount: Double? = it.child("assetAmount").getValue<Double>()
                    var assetUnitPrice: Double? = it.child("assetUnitPrice").getValue<Double>()
                    var assetTotalValue: Double = (assetAmount?.times(assetUnitPrice!!) ?:
                    Log.d("", "")) as Double

                    assetAmount?.let { it1 ->
                        if (assetId != null) {
                            if (assetName != null) {
                                assets.add(Asset(
                                    assetId, assetName, 0.0, "volume", "HTTP",
                                    it1,
                                    assetTotalValue
                                ))
                            }
                        }
                    }

                }


                if (assets.isNotEmpty()) {


                    var walletAdapter: WalletAdapter? =
                        assets?.let { WalletAdapter(assets, this@Wallet) }

                    recyclerView.adapter = walletAdapter

                    hideProgressBarLoading(progressBar)

                } else {
                    Toast.makeText(this, "Failed to retrieve Cryptos!!!", Toast.LENGTH_LONG).show()
                }

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

    override fun onItemClick(item: Asset, absoluteAdapterPosition: Int) {

        if(item.assetId != "USD") {
            val intent: Intent = Intent(this, Trade::class.java)

            intent.putExtra("ASSET", item)
            intent.putExtra("USER_UID", USER_UID)

            startActivity(intent)
        }
        else{
            Toast.makeText(this, getString(R.string.cannot_trade_usd), Toast.LENGTH_SHORT).show()
//            val intent: Intent = Intent(this, Wallet::class.java)
//            intent.putExtra("USER_UID", USER_UID)
//            startActivity(intent)
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}