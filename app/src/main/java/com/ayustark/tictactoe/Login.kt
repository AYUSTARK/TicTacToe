package com.ayustark.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var myRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

    }

    fun buLoginEvent(view: View) {
        loginToFireBase(etEmail.text.toString(), etPassword.text.toString())
    }

    private fun loginToFireBase(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG).show()
                    val currentUser = mAuth!!.currentUser
                    //save in database
                    if (currentUser != null) {
                        myRef.child("Users").child(splitString(currentUser.email!!.toString()))
                            .child("Request").setValue(currentUser.uid)
                    }
                    loadMain()
                } else {
                    Log.e("Login", "${task.exception}")
                    Toast.makeText(applicationContext, "fail login", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        loadMain()
    }

    private fun loadMain() {
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
            finish()
        }
    }

    private fun splitString(str: String): String {
        val split = str.split("@")
        return split[0]
    }

}

