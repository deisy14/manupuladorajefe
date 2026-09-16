package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.databinding.ActivityInventarioBinding

class InventarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBarraInferior()
    }

    private fun configurarBarraInferior() {

        // INICIO
        findViewById<android.view.View>(R.id.navInicio).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MenuDiarioActivity::class.java
                )
            )
        }


        // INVENTARIO
        findViewById<android.view.View>(R.id.navInventario).setOnClickListener {

            // Ya estamos en Inventario
        }


        // PREPARACIONES
        findViewById<android.view.View>(R.id.navPreparaciones).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PreparacionesActivity::class.java
                )
            )
        }


        // MANIPULADORAS
        findViewById<android.view.View>(R.id.navManipuladoras).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ManipuladorasActivity::class.java
                )
            )
        }


        // PERFIL
        findViewById<android.view.View>(R.id.navPerfil).setOnClickListener {

            // Perfil lo hacemos después.
        }
    }
}