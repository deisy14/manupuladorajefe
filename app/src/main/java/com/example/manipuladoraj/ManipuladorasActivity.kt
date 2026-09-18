package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ManipuladorasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manipuladoras)

        configurarBarraInferior()

        configurarBotones()
    }

    private fun configurarBarraInferior() {

        // =========================
        // INICIO
        // =========================

        findViewById<View>(R.id.navInicio).setOnClickListener {

            val intent = Intent(
                this,
                MenuDiarioActivity::class.java
            )

            startActivity(intent)
        }


        findViewById<View>(R.id.navInventario).setOnClickListener {

            val intent = Intent(
                this,
                InventarioActivity::class.java
            )

            startActivity(intent)
        }


        findViewById<View>(R.id.navPreparaciones).setOnClickListener {

            val intent = Intent(
                this,
                PreparacionesActivity::class.java
            )

            startActivity(intent)
        }



        findViewById<View>(R.id.navManipuladoras).setOnClickListener {

            // Ya estamos en esta pantalla
        }


        findViewById<View>(R.id.navPerfil).setOnClickListener {

            // Lo dejamos preparado para cuando hagamos Perfil
        }
    }


    private fun configurarBotones() {

        // =========================
        // ASIGNAR TAREAS
        // =========================

        findViewById<TextView>(R.id.btnAsignarTareas).setOnClickListener {

            // Después conectaremos esta acción
            // con la pantalla de asignación de tareas.

        }
    }
}