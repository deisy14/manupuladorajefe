
package com.example.manipuladoraj

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.api.EnviarNotificacionRequest
import com.example.manipuladoraj.api.RetrofitClient
import com.example.manipuladoraj.model.Notificacion
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificacionesActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var estado: TextView
    private lateinit var api: com.example.manipuladoraj.api.ApiService
    private lateinit var campoTitulo: EditText
    private lateinit var campoMensaje: EditText
    private lateinit var botonEnviar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        contenedor = findViewById(R.id.contenedorNotificaciones)
        estado = findViewById(R.id.txtEstadoNotificaciones)
        campoTitulo = findViewById(R.id.edtTituloNotificacion)
        campoMensaje = findViewById(R.id.edtMensajeNotificacion)
        botonEnviar = findViewById(R.id.btnEnviarNotificacion)

        api = RetrofitClient.getApiService(this)

        botonEnviar.setOnClickListener {
            enviarNotificacion()
        }

        findViewById<Button>(R.id.btnMarcarTodasLeidas)
            .setOnClickListener {
                api.marcarTodasLeidas()
                    .enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                        ) {
                            if (response.isSuccessful) {
                                cargarNotificaciones()
                            } else {
                                estado.text =
                                    "No se pudieron marcar como leídas (${response.code()})."
                            }
                        }

                        override fun onFailure(
                            call: Call<JsonObject>,
                            t: Throwable
                        ) {
                            estado.text =
                                "Error de conexión: ${t.message}"
                        }
                    })
            }

        cargarNotificaciones()
    }

    private fun enviarNotificacion() {
        val titulo = campoTitulo.text.toString().trim()
        val mensaje = campoMensaje.text.toString().trim()

        if (titulo.isEmpty()) {
            campoTitulo.error = "Escribe el título"
            campoTitulo.requestFocus()
            return
        }

        if (mensaje.isEmpty()) {
            campoMensaje.error = "Escribe el mensaje"
            campoMensaje.requestFocus()
            return
        }

        botonEnviar.isEnabled = false
        estado.text = "Enviando notificación..."

        val datos = EnviarNotificacionRequest(
            titulo = titulo,
            mensaje = mensaje
        )

        api.enviarNotificacion(datos)
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    botonEnviar.isEnabled = true

                    if (response.isSuccessful) {
                        val resultado = response.body()
                        val mensajeServidor = resultado
                            ?.get("mensaje")
                            ?.takeIf { it.isJsonPrimitive }
                            ?.asString

                        val cantidad = resultado
                            ?.get("cantidad_destinatarios")
                            ?.takeIf { it.isJsonPrimitive }
                            ?.asInt

                        estado.text = buildString {
                            append(
                                mensajeServidor
                                    ?: "El servidor aceptó la notificación."
                            )
                            if (cantidad != null) {
                                append(" Destinatarios: $cantidad.")
                            }
                        }

                        campoTitulo.text.clear()
                        campoMensaje.text.clear()
                        cargarNotificaciones()
                    } else {
                        val detalle = try {
                            response.errorBody()?.string()
                        } catch (_: Exception) {
                            null
                        }

                        estado.text =
                            "No se pudo enviar (${response.code()}). " +
                            (detalle ?: "Revisa tu sesión y los permisos.")
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {
                    botonEnviar.isEnabled = true
                    estado.text =
                        "Error de conexión al enviar: ${t.message}"
                }
            })
    }

    private fun cargarNotificaciones() {
        estado.text = "Cargando notificaciones..."

        api.obtenerNotificaciones()
            .enqueue(object : Callback<List<Notificacion>> {

                override fun onResponse(
                    call: Call<List<Notificacion>>,
                    response: Response<List<Notificacion>>
                ) {
                    if (!response.isSuccessful) {
                        estado.text =
                            "No se pudieron cargar (${response.code()}). " +
                            "Verifica tu sesión."
                        return
                    }

                    contenedor.removeAllViews()
                    val lista = response.body().orEmpty()

                    if (lista.isEmpty()) {
                        estado.text = "No tienes notificaciones."
                        return
                    }

                    estado.text = "${lista.size} notificación(es)"

                    lista.forEach { notificacion ->
                        val tarjeta =
                            LinearLayout(this@NotificacionesActivity).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding(24, 20, 24, 20)
                                setBackgroundColor(0xFFF3F3F3.toInt())
                            }

                        val titulo =
                            TextView(this@NotificacionesActivity).apply {
                                text = notificacion.titulo
                                textSize = 18f
                            }

                        val mensaje =
                            TextView(this@NotificacionesActivity).apply {
                                text = notificacion.mensaje
                                textSize = 15f
                            }

                        val fecha =
                            TextView(this@NotificacionesActivity).apply {
                                text = notificacion.fecha_hora ?: ""
                                textSize = 12f
                            }

                        tarjeta.addView(titulo)
                        tarjeta.addView(mensaje)
                        tarjeta.addView(fecha)

                        if (!notificacion.leida) {
                            tarjeta.addView(
                                Button(this@NotificacionesActivity).apply {
                                    text = "Marcar como leída"
                                    setOnClickListener {
                                        marcarLeida(
                                            notificacion.id_notificacion
                                        )
                                    }
                                }
                            )
                        }

                        val parametros = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 20)
                        }

                        contenedor.addView(tarjeta, parametros)
                    }
                }

                override fun onFailure(
                    call: Call<List<Notificacion>>,
                    t: Throwable
                ) {
                    estado.text =
                        "Error al cargar notificaciones: ${t.message}"
                }
            })
    }

    private fun marcarLeida(id: Int) {
        api.marcarNotificacionLeida(id)
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        cargarNotificaciones()
                    } else {
                        estado.text =
                            "No se pudo marcar como leída (${response.code()})."
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    t: Throwable
                ) {
                    estado.text =
                        "Error de conexión: ${t.message}"
                }
            })
    }
}
