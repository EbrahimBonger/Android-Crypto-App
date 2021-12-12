package com.example.androidcrypto.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.androidcrypto.R
//import com.example.androidcrypto.function.FunctionUtil.readDatabase
//import com.example.androidcrypto.function.FunctionUtil.writeNewUser
import com.example.androidcrypto.transaction.FunctionUtil.readDatabase
import com.example.androidcrypto.transaction.FunctionUtil.writeNewUser
import com.example.androidcrypto.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    // For an explaination of why lateinit var is needed, see:
    // https://docs.google.com/presentation/d/1icewQjn-fkd-wTepzRoqXOjaKWtGUrx0o0Us2anJz3w/edit#slide=id.g615c45607e_0_156

    private lateinit var username: EditText
    private lateinit var password: EditText
        private lateinit var re_password: EditText
    private lateinit var signUp: Button
    private lateinit var back: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var user: User

    // onCreate is called the first time the Activity is to be shown to the user, so it a good spot
    // to put initialization logic.
    // https://developer.android.com/guide/components/activities/activity-lifecycle
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tells Android which layout file should be used for this screen.
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()

        // Equivalent of a System.out.println (Android has different logging levels to organize logs -- .d is for DEBUG)
        // First parameter = the "tag" allows you to find related logging statements easier (e.g. all logs in the MainActivity)
        // Second parameter = the actual thing you want to log

        val preferences: SharedPreferences = getSharedPreferences("android-crypto", Context.MODE_PRIVATE)


        // The IDs we are using here should match what was set in the "id" field for our views
        // in our XML layout (which was specified by setContentView).
        // Android will "search" the UI for the elements with the matching IDs to bind to our variables.

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        re_password = findViewById(R.id.re_password)
        signUp = findViewById(R.id.signUp)
        back = findViewById(R.id.back)
        progressBar = findViewById(R.id.authenticationProgressBar)

        // Kotlin shorthand for login.setEnabled(false).
        // If the getter / setter is unambiguous, Kotlin lets you use the property-style syntax
        back.isEnabled = true
        signUp.isEnabled = false

        // Restore the saved username from SharedPreferences and display it to the user when the screen loads.
        // Default to the empty string if there is no saved username.
//        val savedUsername = preferences.getString("USERNAME", "")
//        username.setText(savedUsername)


        signUp.setOnClickListener {


            val inputtedUsername: String = username.text.toString()
            val inputtedPassword: String = password.text.toString()
            val reInputtedPassword: String = re_password.text.toString()

            if(inputtedPassword == reInputtedPassword) {
                showLoading()
                firebaseAuth
                    .createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        hideLoading()



                        if (task.isSuccessful) {
                            val currentUser: FirebaseUser = firebaseAuth.currentUser!!
                            writeNewUser(currentUser.uid)
                            readDatabase(currentUser.uid)
                            Toast.makeText(
                                this,
                                getString(R.string.signup_success, currentUser.email),
                                Toast.LENGTH_LONG
                            ).show()

                            // getString(R.string.login_success, currentUser.email)

                            val intent: Intent = Intent(this, LogIn::class.java)
                            startActivity(intent)
                        }
                        else {
                            val exception: Exception? = task.exception
                            when (exception) {
                                is FirebaseAuthWeakPasswordException -> {
                                    Toast.makeText(
                                        this,
                                        R.string.signup_failure_weak_password,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                is FirebaseAuthUserCollisionException -> {
                                    Toast.makeText(
                                        this,
                                        R.string.signup_failure_already_exists,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent: Intent = Intent(this, LogIn::class.java)
                                    startActivity(intent)
                                }
                                is FirebaseAuthInvalidCredentialsException -> {
                                    Toast.makeText(
                                        this,
                                        R.string.signup_failure_invalid_format,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.signup_failure_generic, exception),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
            }
            else {
                Toast.makeText(this, R.string.password_has_not_matched, Toast.LENGTH_LONG).show()
                val intent: Intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }
        }

        back.setOnClickListener {
            val intent: Intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        // Using the same TextWatcher instance for both EditTexts so the same block of code runs on each character.
        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
        re_password.addTextChangedListener(textWatcher)
    }

    // Displays the loading indicator and disables user input
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        signUp.isEnabled = false
//        back.isEnabled = false
        username.isEnabled = false
        password.isEnabled = false
        re_password.isEnabled = false
    }

    // Hides the loading indicator and enables user input
    private fun hideLoading() {
        progressBar.visibility = View.INVISIBLE
//        back.isEnabled = true
        signUp.isEnabled = true
        username.isEnabled = true
        password.isEnabled = true
        re_password.isEnabled = true
    }


    // Another example of explicitly implementing an interface (TextWatcher). We cannot use
    // a lambda in this case since there are multiple functions we need to implement.
    //
    // We're defining an "anonymous class" here using the `object` keyword (basically creating
    // a new, dedicated object to implement a TextWatcher for this variable assignment).
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Kotlin shorthand for username.getText().toString()
            // .toString() is needed because getText() returns an Editable (basically a char array).

            val inputtedUsername: String = username.text.toString()
            val inputtedPassword: String = password.text.toString()
            val reInputtedPassword: String = re_password.text.toString()

            val enableButton: Boolean = inputtedUsername.isNotBlank() && inputtedPassword.isNotBlank() && reInputtedPassword.isNotBlank()



            // Kotlin shorthand for login.setEnabled(enableButton)
//            back.isEnabled = enableButton
            signUp.isEnabled = enableButton
        }

        override fun afterTextChanged(p0: Editable?) {}
    }
}