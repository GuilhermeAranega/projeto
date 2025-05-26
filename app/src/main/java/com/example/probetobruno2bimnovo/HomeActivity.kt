package com.example.probetobruno2bimnovo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HomeActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private var APIKEY = "AIzaSyB-LxTMLEwnUWMbk2Km6aboSGUjT-iHuoI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        FirebaseApp.initializeApp(this)

        val btnSair = findViewById<Button>(R.id.btnSair)
        val btnGerarDraminha = findViewById<Button>(R.id.btnGerarDraminha)
        val btnGerarSertanejo = findViewById<Button>(R.id.btnGerarSertanejo)
        val btnGerarRomeu = findViewById<Button>(R.id.btnGerarRomeu)
        val txtResposta = findViewById<TextView>(R.id.txtResposta)
        val eTxtpalavras = findViewById<EditText>(R.id.eTxtPalavras)
        var palavras = ""

        btnSair.setOnClickListener {
            LogOut()
        }

        btnGerarDraminha.setOnClickListener {
            palavras = eTxtpalavras.text.toString().trim()
            Toast.makeText(this, "Gerando poema com as palavras $palavras...", Toast.LENGTH_SHORT).show()
            gerarPoema("draminha", palavras) {
                resposta -> runOnUiThread {
                    txtResposta.text = resposta
                }
            }

        }

        btnGerarSertanejo.setOnClickListener {
            palavras = eTxtpalavras.text.toString().trim()
            Toast.makeText(this, "Gerando poema com as palavras $palavras...", Toast.LENGTH_SHORT).show()
            gerarPoema("sertanejo", palavras) {
                resposta -> runOnUiThread {
                    txtResposta.text = resposta
                }
            }

        }

        btnGerarRomeu.setOnClickListener {
            palavras = eTxtpalavras.text.toString().trim()
            Toast.makeText(this, "Gerando poema com as palavras $palavras...", Toast.LENGTH_SHORT).show()
            gerarPoema("romeu", palavras) {
                resposta -> runOnUiThread {
                    txtResposta.text = resposta
                }
            }
        }

    }

    fun LogOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(this@HomeActivity, MainActivity::class.java)
        startActivity(intent)
    }


    private fun gerarPoema(tipo: String, palavras: String, callback: (String) -> Unit) {
        val URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$APIKEY"
        var json = JSONObject()
        if (tipo == "draminha") {
            json = JSONObject().apply {
                put(
                    "contents", JSONArray().put(
                        JSONObject().put(
                            "parts", JSONArray().put(
                                JSONObject().put("text", "Você é um poeta dramático que escreve versos curtos e intensos sobre saudade, desilusão ou solidão.\n" +
                                        "Gere uma poesia com base nas palavras: $palavras.\n" +
                                        "Use metáforas exageradas, emoção à flor da pele e imagens melancólicas.\n" +
                                        "O poema deve ter até 4 versos. Termine com um sentimento de perda ou saudade. \n" +
                                        "Responda apenas com os versos, sem introdução ou explicações.")
                            )
                        )
                    )
                )
            }
        } else if (tipo == "sertanejo") {
            json = JSONObject().apply {
                put(
                    "contents", JSONArray().put(
                        JSONObject().put(
                            "parts", JSONArray().put(
                                JSONObject().put("text", "Você é um cantor sertanejo universitário compondo uma música sobre amor, saudade ou traição.\n" +
                                        "Crie uma letra curta baseada nas palavras: $palavras.\n" +
                                        "Use rimas simples, tom popular e expressões típicas do sertanejo moderno.\n" +
                                        "Pode citar bebida, saudade da ex, ou mensagens de WhatsApp não respondidas.\n" +
                                        "Estilo inspirado em Jorge & Mateus ou Gustavo Mioto.\n" +
                                        "Máximo de 4 versos.\n" +
                                        "Responda apenas com os versos, sem introdução ou explicações.")
                            )
                        )
                    )
                )
            }
        } else {
            json = JSONObject().apply {
                put(
                    "contents", JSONArray().put(
                        JSONObject().put(
                            "parts", JSONArray().put(
                                JSONObject().put("text", "Escreva um poema romântico e dramático no estilo de Romeu e Julieta, com linguagem poética e inspirada no amor impossível.\n" +
                                        "Use as palavras: $palavras para criar uma poesia trágica e intensa.\n" +
                                        "O tom deve ser clássico, apaixonado e elegante, como se fosse parte de uma peça de teatro.\n" +
                                        "Utilize até 4 versos e evite linguagem moderna.\n" +
                                        "Responda apenas com os versos, sem introdução ou explicações.")
                            )
                        )
                    )
                )
            }
        }

        val mediaType = "application/json".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(URL)
            .post(body)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Erro: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val resposta = response.body?.string() ?: "Sem resposta"
                try {
                    val jsonResposta = JSONObject(resposta)
                    if (!jsonResposta.has("candidates")) {
                        callback("Erro: resposta inesperada da API: $resposta")
                        return
                    }
                    val texto = jsonResposta
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    callback(texto)
                } catch (e: Exception) {
                    callback("Erro ao ler resposta: ${e.message}")
                }
            }
        })
    }




}