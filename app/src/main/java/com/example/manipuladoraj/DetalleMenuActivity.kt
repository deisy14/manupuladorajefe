package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalleMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalle_menu)

        configurarBotones()
    }

    private fun configurarBotones() {

        // =========================
        // BOTÓN VOLVER
        // =========================

        val btnVolver = findViewById<ImageView>(R.id.btnVolver)

        btnVolver.setOnClickListener {

            finish()
        }


        // =========================
        // ELEGIR ESTE MENÚ
        // =========================

        val btnElegirMenu =
            findViewById<TextView>(R.id.btnElegirMenu)

        btnElegirMenu.setOnClickListener {

            val intent = Intent(
                this,
                ConfirmarMenuActivity::class.java
            )

            startActivity(intent)
        }
    }
}