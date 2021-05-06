package com.ayustark.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : androidx.appcompat.app.AppCompatActivity() {
    //database instance
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var myEmail: String? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this@MainActivity)
        val b: Bundle = intent.extras!!
        myEmail = b.getString("email")
        incomingCalls()
    }

    fun buClick(view: View) {
        val buSelected = view as Button
        var cellID = 0
        when (buSelected.id) {
            R.id.bu1 -> cellID = 1
            R.id.bu2 -> cellID = 2
            R.id.bu3 -> cellID = 3
            R.id.bu4 -> cellID = 4
            R.id.bu5 -> cellID = 5
            R.id.bu6 -> cellID = 6
            R.id.bu7 -> cellID = 7
            R.id.bu8 -> cellID = 8
            R.id.bu9 -> cellID = 9
        }
        // Toast.makeText(this,"ID:"+ cellID, Toast.LENGTH_LONG).show()
        myRef.child("PlayerOnline").child(sessionID!!).child(cellID.toString()).setValue(myEmail)
    }

    var player1 = java.util.ArrayList<Int>()
    var player2 = java.util.ArrayList<Int>()
    var activePlayer = 1

    private fun playGame(cellID: Int, buSelected: Button) {
        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellID)
            activePlayer = 2
        } else {
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.darkgreen)
            player2.add(cellID)
            activePlayer = 1
        }
        buSelected.isEnabled = false
        checkWinner()
    }

    private fun checkWinner() {
        var winer = -1
        // row 1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winer = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winer = 2
        }
        // row 2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winer = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winer = 2
        }
        // row 3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winer = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winer = 2
        }
        // col 1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winer = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winer = 2
        }
        // col 2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winer = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winer = 2
        }
        // col 3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winer = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winer = 2
        }
        if (winer != -1) {
            if (winer == 1) {
                Toast.makeText(
                    this,
                    " Player 1  win the game",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    " Player 2  win the game",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun autoPlay(cellID: Int) {
        val buSelect: Button? = when (cellID) {
            1 -> bu1
            2 -> bu2
            3 -> bu3
            4 -> bu4
            5 -> bu5
            6 -> bu6
            7 -> bu7
            8 -> bu8
            9 -> bu9
            else -> {
                bu1
            }
        }
        if (buSelect != null) {
            playGame(cellID, buSelect)
        }
    }

    fun buRequestEvent(view: View) {
        val userEmail = etEmail.text.toString()
        myRef.child("Users").child(splitString(userEmail)).child("Request").push()
            .setValue(myEmail)
        playerOnline(splitString(myEmail!!) + splitString(userEmail))
        playerSymbol = "X"
    }

    fun buAcceptEvent(view: View) {
        val userEmail = etEmail.text.toString()
        myRef.child("Users").child(splitString(userEmail)).child("Request").push()
            .setValue(myEmail)
        playerOnline(splitString(userEmail) + splitString(myEmail!!))
        playerSymbol = "O"
    }

    private var sessionID: String? = null
    var playerSymbol: String? = null
    private fun playerOnline(sessionID: String) {
        this.sessionID = sessionID
        myRef.child("PlayerOnline").removeValue()
        myRef.child("PlayerOnline").child(sessionID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        player1.clear()
                        player2.clear()
                        val td = dataSnapshot.value as HashMap<String, Any>
                        var value: String
                        for (key in td.keys) {
                            value = td[key] as String
                            activePlayer = if (value != myEmail) {
                                if (playerSymbol === "X") 1 else 2
                            } else {
                                if (playerSymbol === "X") 2 else 1
                            }
                            autoPlay(key.toInt())
                        }
                    } catch (ex: Exception) {
                    }
                }

                override fun onCancelled(p0: DatabaseError) {}
            })
    }

    var number = 0
    private fun incomingCalls() {
        myRef.child("Users").child(splitString(myEmail!!)).child("Request")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        val td = dataSnapshot.value as HashMap<String, Any>
                        val value: String
                        for (key in td.keys) {
                            value = td[key] as String
                            etEmail.setText(value)
                            val notifyMe = Notifications()
                            notifyMe.notify(
                                applicationContext,
                                "$value want to play tic tac toy",
                                number
                            )
                            number++
                            myRef.child("Users").child(splitString(myEmail!!)).child("Request")
                                .setValue(true)
                            break
                        }
                    } catch (ex: Exception) {
                    }
                }

                override fun onCancelled(p0: DatabaseError) {}
            })
    }

    fun splitString(str: String): String {
        val split = str.split("@")
        return split[0]
    }
}