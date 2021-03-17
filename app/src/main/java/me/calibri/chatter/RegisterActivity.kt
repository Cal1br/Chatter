package me.calibri.chatter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

//TODO binder
class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        //правим back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        register_button.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        if (username == "" || username == "null") {
            Toast.makeText(
                this@RegisterActivity,
                "Username cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        } else if (email == "" || email == "null") {
            Toast.makeText(this@RegisterActivity, "Email cannot be empty", Toast.LENGTH_SHORT)
                .show()

        } else if (password == "" || password == "null") {
            Toast.makeText(
                this@RegisterActivity,
                "Password cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password) //уау, това е прекалено лесно ^.^
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("users")
                            .child(firebaseUserID)
                        val userHashMap = HashMap<String, Any>()
                        userHashMap["username"] = username
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["profile"] =
                            "https://firebasestorage.googleapis.com/v0/b/chatter-b221e.appspot.com/o/defaultProfile.png?alt=media&token=61ff6f44-0aac-4fef-8fe7-63dba9693545"
                        userHashMap["cover"] =
                            "https://firebasestorage.googleapis.com/v0/b/chatter-b221e.appspot.com/o/defaultCover.jpg?alt=media&token=2cfa985e-be86-45e2-8f6d-d06021314d86"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["tumblr"] = "https://m.tumblr.com/"
                        userHashMap["pinterest"] = "https://m.pinterest.com/"
                        userHashMap["website"] = "https://www.google.com"
                        //default инициализация
                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    //TODO 19:00 5то клипче, не разбрах какво прави това, и да го коментирам
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error Message: " + task.exception?.message.toString()
                            , Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
