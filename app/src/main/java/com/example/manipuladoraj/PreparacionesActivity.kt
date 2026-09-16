package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.databinding.ActivityPreparacionesBinding

class PreparacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreparacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreparacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBarraInferior()
    }

    private fun configurarBarraInferior() {

        findViewById<View>(R.id.navInicio).setOnClickListener {
            startActivity(
                Intent(this, MenuDiarioActivity::class.java)
            )
        }

        findViewById<View>(R.id.navInventario).setOnClickListener {
            startActivity(
                Intent(this, InventarioActivity::class.java)
            )
        }

        findViewById<View>(R.id.navPreparaciones).setOnClickListener {
            // Ya estamos en Preparaciones
        }

        findViewById<View>(R.id.navManipuladoras).setOnClickListener {
            startActivity(
                Intent(this, ManipuladorasActivity::class.java)
            )
        }

        findViewById<View>(R.id.navPerfil).setOnClickListener {
            // Pendiente
        }
    }
}