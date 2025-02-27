package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var loadingProgressBar: FrameLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var buttonLogin: ImageView
    private lateinit var buttonRegister: TextView
    var monster: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //initalize
        auth = FirebaseAuth.getInstance()
        buttonLogin = findViewById(R.id.btnlogin)
        buttonRegister = findViewById(R.id.tv_register_prompt)
        loginUsername = findViewById(R.id.et_namaakun)
        loginPassword = findViewById(R.id.et_kelas)
        loadingProgressBar = findViewById(R.id.login_loading_progress_bar)

        buttonLogin.setOnClickListener {
            val username = loginUsername.text.toString()
            val password = loginPassword.text.toString()
            loginUser(username, password)
        }

        buttonRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            finish()
        }

    }

    private fun loginUser(username: String, password: String) {
        loadingProgressBar.visibility = View.VISIBLE
        if (username.isEmpty() || password.isEmpty()){
            loadingProgressBar.visibility = View.GONE
            Toast.makeText(this, "Email ataupun password kosong", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    loadingProgressBar.visibility = View.GONE
                    //add interact to homepage
                    val intent = Intent(this, MainMenu::class.java)
                    intent.putExtra("monster", monster)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    finish()
                } else {
                    Toast.makeText(this, "gagal login", Toast.LENGTH_SHORT).show()
                }
            } .addOnFailureListener{
                loadingProgressBar.visibility = View.GONE
                Toast.makeText(this, "gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
    }
}