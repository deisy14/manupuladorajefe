package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_perfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        configurarBotones()
        configurarBarraInferior()
    }

    private fun configurarBotones() {

        // CAMBIAR CONTRASEÑA
        findViewById<View>(R.id.btnCambiarContrasena).setOnClickListener {

            // Aquí después podemos poner la pantalla
            // para cambiar la contraseña.
        }


        // CERRAR SESIÓN
        findViewById<View>(R.id.btnCerrarSesion).setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }


    private fun configurarBarraInferior() {

        // INICIO
        findViewById<View>(R.id.navInicio).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MenuDiarioActivity::class.java
                )
            )
        }


        // INVENTARIO
        findViewById<View>(R.id.navInventario).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    InventarioActivity::class.java
                )
            )
        }


        // PREPARACIONES
        findViewById<View>(R.id.navPreparaciones).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PreparacionesActivity::class.java
                )
            )
        }


        // MANIPULADORAS
        findViewById<View>(R.id.navManipuladoras).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ManipuladorasActivity::class.java
                )
            )
        }


        // PERFIL
        findViewById<View>(R.id.navPerfil).setOnClickListener {

            // Ya estamos en Perfil
        }
    }
}