package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmarMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_confirmar_menu)

        configurarBotones()
    }

    private fun configurarBotones() {

        // BOTÓN VOLVER
        val btnVolver = findViewById<ImageView>(R.id.btnVolver)

        btnVolver.setOnClickListener {
            finish()
        }


        // BOTÓN CANCELAR
        val btnCancelar = findViewById<TextView>(R.id.btnCancelar)

        btnCancelar.setOnClickListener {
            finish()
        }


        // BOTÓN CONFIRMAR
        val btnConfirmar = findViewById<TextView>(R.id.btnConfirmar)

        btnConfirmar.setOnClickListener {

            val intent = Intent(this, MenuAsignadoActivity::class.java)

            startActivity(intent)

            finish()
        }
    }
}