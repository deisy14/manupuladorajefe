package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.databinding.ActivityMenuDiarioBinding

class MenuDiarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuDiarioBinding

    // Posición actual del carrusel
    private var posicionPlato = 0

    // Platos del carrusel
    private val imagenesPlatos = intArrayOf(
        R.drawable.frijoles,
        R.drawable.frijoles,
        R.drawable.apanado,
        R.drawable.frijoles,
        R.drawable.frijoles
    )

    private val nombresPlatos = arrayOf(
        "Frijoles",
        "Frijoles",
        "Pollo guisado",
        "Frijoles",
        "Frijoles"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuDiarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotonesComida()
        configurarCarrusel()
        configurarBarraInferior()
        configurarNotificacion()
    }

    // ---------------------------------------------------------
    // DESAYUNO / ALMUERZO
    // ---------------------------------------------------------

    private fun configurarBotonesComida() {

        binding.btnAlmuerzo.setOnClickListener {

            binding.btnAlmuerzo.setBackgroundResource(
                R.drawable.bg_boton_amarillo
            )

            binding.btnDesayuno.setBackgroundResource(
                R.drawable.bg_boton_outline
            )
        }

        binding.btnDesayuno.setOnClickListener {

            binding.btnDesayuno.setBackgroundResource(
                R.drawable.bg_boton_amarillo
            )

            binding.btnAlmuerzo.setBackgroundResource(
                R.drawable.bg_boton_outline
            )
        }
    }

    // ---------------------------------------------------------
    // CARRUSEL DE PLATOS
    // ---------------------------------------------------------

    private fun configurarCarrusel() {

        binding.btnAnterior.setOnClickListener {

            posicionPlato--

            if (posicionPlato < 0) {
                posicionPlato = imagenesPlatos.size - 1
            }

            actualizarPlato()
        }

        binding.btnSiguiente.setOnClickListener {

            posicionPlato++

            if (posicionPlato >= imagenesPlatos.size) {
                posicionPlato = 0
            }

            actualizarPlato()
        }
    }

    private fun actualizarPlato() {

        binding.imgPlatoPrincipal.setImageResource(
            imagenesPlatos[posicionPlato]
        )

        binding.tvPlatoSeleccionado.text =
            nombresPlatos[posicionPlato]

        // Actualiza las imágenes laterales
        val posicionAnterior =
            if (posicionPlato == 0) {
                imagenesPlatos.size - 1
            } else {
                posicionPlato - 1
            }

        val posicionSiguiente =
            if (posicionPlato == imagenesPlatos.size - 1) {
                0
            } else {
                posicionPlato + 1
            }

        binding.imgLateralIzq.setImageResource(
            imagenesPlatos[posicionAnterior]
        )

        binding.imgLateralDer.setImageResource(
            imagenesPlatos[posicionSiguiente]
        )
    }

    // ---------------------------------------------------------
    // NOTIFICACIÓN
    // ---------------------------------------------------------

    private fun configurarNotificacion() {

        binding.btnNotificacion.setOnClickListener {

            // Por ahora no abre otra pantalla.
            // Aquí posteriormente podemos colocar
            // la pantalla de notificaciones.
        }
    }

    // ---------------------------------------------------------
    // BARRA INFERIOR
    // ---------------------------------------------------------

    private fun configurarBarraInferior() {

        findViewById<View>(R.id.navInicio).setOnClickListener {

            // Ya estamos en Inicio
        }

        findViewById<View>(R.id.navInventario).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    InventarioActivity::class.java
                )
            )
        }

        findViewById<View>(R.id.navPreparaciones).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PreparacionesActivity::class.java
                )
            )
        }

        findViewById<View>(R.id.navManipuladoras).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ManipuladorasActivity::class.java
                )
            )
        }

        findViewById<View>(R.id.navPerfil).setOnClickListener {

            // Pendiente de crear la pantalla Perfil
        }
    }
}