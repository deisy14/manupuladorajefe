
package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.api.RetrofitClient
import com.example.manipuladoraj.databinding.ActivityMenuDiarioBinding
import com.example.manipuladoraj.model.DetallePlato
import com.example.manipuladoraj.model.IngredienteSugerido
import com.example.manipuladoraj.model.MenuPae
import com.example.manipuladoraj.model.Plato
import com.example.manipuladoraj.model.SugerenciaRecetaRequest
import com.example.manipuladoraj.model.SugerenciaRecetaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuDiarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuDiarioBinding

    // Posición actual del carrusel
    private var posicionPlato = 0

    // Imágenes de los platos
    private val imagenesPlatos = intArrayOf(
        R.drawable.frijoles,
        R.drawable.frijoles,
        R.drawable.apanado,
        R.drawable.frijoles,
        R.drawable.frijoles
    )

    // Nombres de los platos
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
        binding.btnSugerenciaIa.setOnClickListener {
            cargarOpcionesSugerencia()
        }

        // Cargar los menús desde el backend
        cargarMenusRemotos()
    }

    // ---------------------------------------------------------
    // CONSUMO DE LA API DE MENÚS
    // ---------------------------------------------------------

    private fun cargarMenusRemotos() {

        RetrofitClient.getApiService(this)
            .obtenerMenus()
            .enqueue(object : Callback<List<MenuPae>> {

                override fun onResponse(
                    call: Call<List<MenuPae>>,
                    response: Response<List<MenuPae>>
                ) {
                    if (response.isSuccessful) {

                        val listaMenus = response.body()

                        Log.d(
                            "API_MENUS",
                            "Menús recibidos: $listaMenus"
                        )

                        Toast.makeText(
                            this@MenuDiarioActivity,
                            "Menús cargados: ${listaMenus?.size ?: 0}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Log.e(
                            "API_MENUS",
                            "Error HTTP: ${response.code()} - ${response.message()}"
                        )

                        Toast.makeText(
                            this@MenuDiarioActivity,
                            "Error al cargar menús: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<MenuPae>>,
                    t: Throwable
                ) {
                    Log.e(
                        "API_MENUS",
                        "Error de conexión: ${t.message}",
                        t
                    )

                    Toast.makeText(
                        this@MenuDiarioActivity,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun cargarOpcionesSugerencia() {
        binding.btnSugerenciaIa.isEnabled = false
        val api = RetrofitClient.getApiService(this)

        api.obtenerMenus().enqueue(object : Callback<List<MenuPae>> {
            override fun onResponse(
                call: Call<List<MenuPae>>,
                response: Response<List<MenuPae>>
            ) {
                if (!response.isSuccessful) {
                    mostrarErrorCarga("menús", response.code())
                    return
                }

                val menus = response.body().orEmpty().filter { it.id_menu != null }
                if (menus.isEmpty()) {
                    mostrarErrorCarga("menús disponibles", null)
                    return
                }

                api.obtenerDetallesPlato().enqueue(object : Callback<List<DetallePlato>> {
                    override fun onResponse(
                        call: Call<List<DetallePlato>>,
                        response: Response<List<DetallePlato>>
                    ) {
                        if (!response.isSuccessful) {
                            mostrarErrorCarga("platos del menú", response.code())
                            return
                        }

                        val detalles = response.body().orEmpty()
                        api.obtenerPlatos().enqueue(object : Callback<List<Plato>> {
                            override fun onResponse(
                                call: Call<List<Plato>>,
                                response: Response<List<Plato>>
                            ) {
                                binding.btnSugerenciaIa.isEnabled = true
                                if (!response.isSuccessful) {
                                    mostrarErrorCarga("platos", response.code())
                                    return
                                }
                                mostrarSelectorMenu(
                                    menus,
                                    detalles,
                                    response.body().orEmpty()
                                )
                            }

                            override fun onFailure(call: Call<List<Plato>>, t: Throwable) {
                                mostrarErrorCarga(t.message ?: "conexión", null)
                            }
                        })
                    }

                    override fun onFailure(call: Call<List<DetallePlato>>, t: Throwable) {
                        mostrarErrorCarga(t.message ?: "conexión", null)
                    }
                })
            }

            override fun onFailure(call: Call<List<MenuPae>>, t: Throwable) {
                mostrarErrorCarga(t.message ?: "conexión", null)
            }
        })
    }

    private fun mostrarSelectorMenu(
        menus: List<MenuPae>,
        detalles: List<DetallePlato>,
        platos: List<Plato>
    ) {
        val etiquetas = menus.map { menu ->
            "Menú #${menu.id_menu} · ${menu.fecha ?: "sin fecha"} · " +
                "${menu.ninos_presentes ?: 0} niños"
        }

        AlertDialog.Builder(this)
            .setTitle("Selecciona un menú")
            .setItems(etiquetas.toTypedArray()) { _, index ->
                val menu = menus[index]
                val menuId = menu.id_menu ?: return@setItems
                mostrarSelectorPlato(menuId, detalles, platos)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarSelectorPlato(
        menuId: Int,
        detalles: List<DetallePlato>,
        platos: List<Plato>
    ) {
        val platosPorId = platos.associateBy { it.id_plato }
        val opciones = detalles
            .filter { it.id_menu == menuId }
            .mapNotNull { detalle ->
                detalle.id_plato?.let { platosPorId[it] }
            }
            .distinctBy { it.id_plato }

        if (opciones.isEmpty()) {
            Toast.makeText(
                this,
                "El menú seleccionado no tiene platos asociados.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Selecciona una preparación")
            .setItems(opciones.map { it.nombre_plato ?: "Plato #${it.id_plato}" }
                .toTypedArray()) { _, index ->
                solicitarSugerencia(menuId, opciones[index].id_plato)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun solicitarSugerencia(menuId: Int, platoId: Int) {
        binding.btnSugerenciaIa.isEnabled = false
        RetrofitClient.getApiService(this)
            .sugerirReceta(SugerenciaRecetaRequest(menuId, platoId))
            .enqueue(object : Callback<SugerenciaRecetaResponse> {
                override fun onResponse(
                    call: Call<SugerenciaRecetaResponse>,
                    response: Response<SugerenciaRecetaResponse>
                ) {
                    binding.btnSugerenciaIa.isEnabled = true
                    val sugerencia = response.body()
                    if (!response.isSuccessful || sugerencia == null) {
                        mostrarErrorCarga("sugerencia IA", response.code())
                        return
                    }
                    mostrarSugerencia(sugerencia)
                }

                override fun onFailure(
                    call: Call<SugerenciaRecetaResponse>,
                    t: Throwable
                ) {
                    mostrarErrorCarga(t.message ?: "conexión", null)
                }
            })
    }

    private fun mostrarSugerencia(sugerencia: SugerenciaRecetaResponse) {
        val contenido = buildString {
            append("Vista previa para ${sugerencia.ninos_presentes} niños.")
            append("\nNo modifica la receta ni el inventario.\n\n")
            sugerencia.ingredientes.forEach { ingrediente ->
                append(formatearIngrediente(ingrediente))
                append("\n\n")
            }
        }.trim()

        val texto = TextView(this).apply {
            text = contenido
            textSize = 16f
            setPadding(48, 24, 48, 24)
        }
        val desplazable = ScrollView(this).apply {
            addView(texto)
        }

        AlertDialog.Builder(this)
            .setTitle("Sugerencia: ${sugerencia.nombre_plato}")
            .setView(desplazable)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun formatearIngrediente(ingrediente: IngredienteSugerido): String {
        val disponibilidad = when {
            !ingrediente.unidad_compatible -> "Unidad incompatible para comparar inventario"
            ingrediente.alcanza -> "Inventario suficiente"
            else -> "Inventario insuficiente"
        }
        val disponible = ingrediente.cantidad_disponible?.let {
            " (disponible: $it ${ingrediente.unidad})"
        }.orEmpty()

        return "• ${ingrediente.nombre}: ${ingrediente.cantidad_total} " +
            "${ingrediente.unidad} total " +
            "(${ingrediente.cantidad_por_nino} por niño)\n$disponibilidad$disponible"
    }

    private fun mostrarErrorCarga(recurso: String, codigo: Int?) {
        binding.btnSugerenciaIa.isEnabled = true
        val detalle = codigo?.let { " Código: $it." }.orEmpty()
        Toast.makeText(
            this,
            "No se pudo cargar $recurso.$detalle",
            Toast.LENGTH_LONG
        ).show()
    }

    // ---------------------------------------------------------
    // BOTONES DE DESAYUNO Y ALMUERZO
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

        // Calcular las posiciones de las imágenes laterales
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
    // NOTIFICACIONES
    // ---------------------------------------------------------

    private fun configurarNotificacion() {

        binding.btnNotificacion.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }
    }

    // ---------------------------------------------------------
    // BARRA DE NAVEGACIÓN INFERIOR
    // ---------------------------------------------------------

    private fun configurarBarraInferior() {

        findViewById<View>(R.id.navInicio).setOnClickListener {
            // Ya estamos en la pantalla de inicio.
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

        findViewById<View>(R.id.navAvisos).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    NotificacionesActivity::class.java
                )
            )
        }

        findViewById<View>(R.id.navPerfil).setOnClickListener {
            // Pendiente de crear la pantalla de perfil.
        }
    }
}
