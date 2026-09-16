package com.example.manipuladoraj

import android.app.Activity
import android.content.Intent
import android.view.View

class BarraNavegacion {

    fun configurar(
        activity: Activity,
        pantallaActual: Class<*>
    ) {

        val navInicio = activity.findViewById<View>(R.id.navInicio)
        val navInventario = activity.findViewById<View>(R.id.navInventario)
        val navPreparaciones = activity.findViewById<View>(R.id.navPreparaciones)
        val navManipuladoras = activity.findViewById<View>(R.id.navManipuladoras)
        val navPerfil = activity.findViewById<View>(R.id.navPerfil)


        // =========================
        // INICIO
        // =========================

        navInicio?.setOnClickListener {

            if (pantallaActual != MenuDiarioActivity::class.java) {

                abrirPantalla(
                    activity,
                    MenuDiarioActivity::class.java
                )
            }
        }


        // =========================
        // INVENTARIO
        // =========================

        navInventario?.setOnClickListener {

            if (pantallaActual != InventarioActivity::class.java) {

                abrirPantalla(
                    activity,
                    InventarioActivity::class.java
                )
            }
        }


        // =========================
        // PREPARACIONES
        // =========================

        navPreparaciones?.setOnClickListener {

            if (pantallaActual != PreparacionesActivity::class.java) {

                abrirPantalla(
                    activity,
                    PreparacionesActivity::class.java
                )
            }
        }


        // =========================
        // MANIPULADORAS
        // =========================

        navManipuladoras?.setOnClickListener {

            if (pantallaActual != ManipuladorasActivity::class.java) {

                abrirPantalla(
                    activity,
                    ManipuladorasActivity::class.java
                )
            }
        }


        // =========================
        // PERFIL
        // =========================

        navPerfil?.setOnClickListener {

            // Lo hacemos después.
        }
    }


    private fun abrirPantalla(
        activity: Activity,
        destino: Class<*>
    ) {

        val intent = Intent(activity, destino)

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        activity.startActivity(intent)

        activity.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}