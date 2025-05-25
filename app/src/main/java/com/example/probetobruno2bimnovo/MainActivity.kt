package com.example.probetobruno2bimnovo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)



        val eTxtEmail = findViewById<EditText>(R.id.eTxtEmail)
        val eTxtSenha = findViewById<EditText>(R.id.eTxtSenha)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnRegistro.setOnClickListener {
            val email = eTxtEmail.text.toString().trim()
            val password = eTxtSenha.text.toString().trim()
            register(email, password)
        }

        btnLogin.setOnClickListener {
            val email = eTxtEmail.text.toString().trim()
            val password = eTxtSenha.text.toString().trim()
            login(email, password)
        }

        seeIfLoggedIn()
    }

    fun seeIfLoggedIn() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    fun register(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro bem-sucedido
                    val user = auth.currentUser
                    Toast.makeText(this, "Registrado com sucesso: ${user?.email}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // Falha no registro
                    Toast.makeText(this, "Erro ao registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    val user = auth.currentUser
                    Toast.makeText(this, "Logado com sucesso: ${user?.email}",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // Falha no login
                    Toast.makeText(this, "Erro ao logar: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}