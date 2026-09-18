package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.api.RetrofitClient
import com.example.manipuladoraj.databinding.ActivityPreparacionesBinding
import com.example.manipuladoraj.model.Ingrediente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreparacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreparacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreparacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBarraInferior()
        cargarIngredientes()
    }

    private fun cargarIngredientes() {

        RetrofitClient.apiService.obtenerIngredientes()
            .enqueue(object : Callback<List<Ingrediente>> {

                override fun onResponse(
                    call: Call<List<Ingrediente>>,
                    response: Response<List<Ingrediente>>
                ) {

                    if (response.isSuccessful) {

                        val ingredientes = response.body()

                        Log.d(
                            "API_INGREDIENTES",
                            "Ingredientes recibidos: $ingredientes"
                        )

                        Toast.makeText(
                            this@PreparacionesActivity,
                            "Ingredientes cargados: ${ingredientes?.size ?: 0}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Log.e(
                            "API_INGREDIENTES",
                            "Error HTTP: ${response.code()}"
                        )

                        Toast.makeText(
                            this@PreparacionesActivity,
                            "Error al cargar ingredientes",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Ingrediente>>,
                    t: Throwable
                ) {

                    Log.e(
                        "API_INGREDIENTES",
                        "Error de conexión: ${t.message}",
                        t
                    )

                    Toast.makeText(
                        this@PreparacionesActivity,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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