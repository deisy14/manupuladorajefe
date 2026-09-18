package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.api.RetrofitClient
import com.example.manipuladoraj.databinding.ActivityInventarioBinding
import com.example.manipuladoraj.model.InventarioItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBarraInferior()

        // ¡Aquí llamamos al consumo de la API al abrir la pantalla!
        cargarInventarioRemoto()
    }

    private fun cargarInventarioRemoto() {
        RetrofitClient.apiService.obtenerInventario()
            .enqueue(object : Callback<List<InventarioItem>> {
                override fun onResponse(
                    call: Call<List<InventarioItem>>,
                    response: Response<List<InventarioItem>>
                ) {
                    if (response.isSuccessful) {
                        val lista = response.body()
                        Log.d("API_INVENTARIO", "Inventario recibido: $lista")

                        Toast.makeText(
                            this@InventarioActivity,
                            "Inventario cargado: ${lista?.size ?: 0} ítems",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e("API_INVENTARIO", "Error HTTP: ${response.code()}")
                        Toast.makeText(
                            this@InventarioActivity,
                            "Error al cargar inventario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<InventarioItem>>, t: Throwable) {
                    Log.e("API_INVENTARIO", "Error de conexión: ${t.message}", t)
                    Toast.makeText(
                        this@InventarioActivity,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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