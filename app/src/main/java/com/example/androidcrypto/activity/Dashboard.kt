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
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidcrypto.R
import com.example.androidcrypto.adapter.DashboardAdapter
import com.example.androidcrypto.adapter.WalletAdapter
import com.example.androidcrypto.adapter.OnCryptoClickListener
import com.example.androidcrypto.api.AssetManager
import com.example.androidcrypto.data.Asset
import com.example.androidcrypto.transaction.FunctionUtil.showProgressBarLoading
import com.example.androidcrypto.transaction.FunctionUtil.hideProgressBarLoading
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync

class Dashboard : AppCompatActivity(), OnCryptoClickListener, AdapterView.OnItemSelectedListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var categorySpinnerList: List<String>
    private lateinit var search_all_btn: Button
    private lateinit var wallet_btn: Button
    var allCategoryNews: List<Asset> = listOf()
    var newsMapCopy: MutableMap<String, MutableList<Asset>> = mutableMapOf()
    private lateinit var spinner: Spinner
    private lateinit var category: String
    private lateinit var topHeadlinesNewsAdapter: WalletAdapter
    private lateinit var assets: List<Asset>
    private var assetManager: AssetManager = AssetManager()
    private lateinit var key: String
    private lateinit var USER_UID: String
    private lateinit var user: FirebaseAuth
    private lateinit var signout_btn: Button
    private lateinit var progressBar: ProgressBar



    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        progressBar = findViewById(R.id.dashboardProgressBar)

        showProgressBarLoading(progressBar)

        user = FirebaseAuth.getInstance()
        if(user.currentUser != null) {
            user.currentUser?.let{

            }
        }


        title = getString(R.string.dashboard_title)

        USER_UID = intent.getStringExtra("USER_UID").toString()

        // go to portfolio page
        wallet_btn = findViewById(R.id.wallet_btn)

        wallet_btn.setOnClickListener {
            val intent: Intent = Intent(this, Wallet::class.java)
            intent.putExtra("USER_UID", USER_UID)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        key = getString(R.string.crypto_general_api_key)
        doAsync {


            assets = try {

                assetManager.retrieveTopHeadlinesNews(key)


            } catch (exception: Exception) {
                val emptyNews: MutableList<Asset> = mutableListOf()
                emptyNews
            }


            runOnUiThread {

                if (assets.isNotEmpty()) {


                    var dashboardAdapter: DashboardAdapter? =
                        assets?.let { DashboardAdapter(assets, this@Dashboard) }

                    recyclerView.adapter = dashboardAdapter

                    hideProgressBarLoading(progressBar)

                } else {
                    Toast.makeText(this@Dashboard, "Failed to retrieve Cryptos!!!", Toast.LENGTH_LONG).show()
                }
            }
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
        val intent: Intent = Intent(this, Trade::class.java)
        intent.putExtra("ASSET", item)
        intent.putExtra("USER_UID", USER_UID)


        startActivity(intent)

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}

