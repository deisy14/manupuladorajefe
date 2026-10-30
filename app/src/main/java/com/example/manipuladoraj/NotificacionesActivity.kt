package com.example.manipuladoraj

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.api.ApiService
import com.example.manipuladoraj.api.Destinatario
import com.example.manipuladoraj.api.RetrofitClient
import com.example.manipuladoraj.model.Notificacion
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.Normalizer

class NotificacionesActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var estado: TextView
    private lateinit var api: ApiService
    private lateinit var campoTitulo: EditText
    private lateinit var campoMensaje: EditText
    private lateinit var botonEnviar: Button
    private lateinit var spnDestinatario: Spinner
    private lateinit var botonAdjuntar: Button
    private lateinit var txtImagenElegida: TextView

    private var imagenUri: Uri? = null
    private var destinatarios: List<Destinatario> = emptyList()
    private var esSupervisor = false

    private val selectorImagen =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagenUri = uri
            txtImagenElegida.text =
                if (uri != null) "Imagen adjunta lista" else "Sin imagen adjunta"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        contenedor = findViewById(R.id.contenedorNotificaciones)
        estado = findViewById(R.id.txtEstadoNotificaciones)
        campoTitulo = findViewById(R.id.edtTituloNotificacion)
        campoMensaje = findViewById(R.id.edtMensajeNotificacion)
        botonEnviar = findViewById(R.id.btnEnviarNotificacion)
        spnDestinatario = findViewById(R.id.spnDestinatario)
        botonAdjuntar = findViewById(R.id.btnAdjuntarImagen)
        txtImagenElegida = findViewById(R.id.txtImagenElegida)

        api = RetrofitClient.getApiService(this)

        val rol = normalizar(
            getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("rol", "") ?: ""
        )
        esSupervisor = rol.contains("supervisor") || rol.contains("coordinador")

        if (esSupervisor) {
            spnDestinatario.visibility = View.VISIBLE
            cargarDestinatarios()
        }

        botonAdjuntar.setOnClickListener {
            selectorImagen.launch("image/*")
        }

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
                            estado.text = "Error de conexión: ${t.message}"
                        }
                    })
            }

        cargarNotificaciones()
    }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
            .trim()

    private fun cargarDestinatarios() {
        api.obtenerDestinatarios()
            .enqueue(object : Callback<List<Destinatario>> {
                override fun onResponse(
                    call: Call<List<Destinatario>>,
                    response: Response<List<Destinatario>>
                ) {
                    if (!response.isSuccessful) {
                        estado.text =
                            "No se pudo cargar la lista de destinatarios (${response.code()})."
                        return
                    }
                    destinatarios = response.body().orEmpty()
                    val nombres = destinatarios.map { "${it.nombre} (${it.rol})" }
                    spnDestinatario.adapter = ArrayAdapter(
                        this@NotificacionesActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        nombres
                    )
                }

                override fun onFailure(
                    call: Call<List<Destinatario>>,
                    t: Throwable
                ) {
                    estado.text = "Error al cargar destinatarios: ${t.message}"
                }
            })
    }

    private fun textoPlano(valor: String): RequestBody =
        RequestBody.create(MediaType.parse("text/plain"), valor)

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

        var idDestinatario: RequestBody? = null
        if (esSupervisor) {
            val posicion = spnDestinatario.selectedItemPosition
            if (destinatarios.isEmpty() || posicion < 0) {
                estado.text = "Selecciona un destinatario."
                return
            }
            idDestinatario =
                textoPlano(destinatarios[posicion].id_usuario.toString())
        }

        var parteImagen: MultipartBody.Part? = null
        val uri = imagenUri
        if (uri != null) {
            try {
                val bytes = contentResolver.openInputStream(uri)
                    ?.use { it.readBytes() }
                if (bytes != null) {
                    val tipo = contentResolver.getType(uri) ?: "image/jpeg"
                    val cuerpo = RequestBody.create(MediaType.parse(tipo), bytes)
                    parteImagen = MultipartBody.Part.createFormData(
                        "imagen",
                        "imagen_notificacion.jpg",
                        cuerpo
                    )
                }
            } catch (e: Exception) {
                estado.text = "No se pudo leer la imagen: ${e.message}"
                return
            }
        }

        botonEnviar.isEnabled = false
        estado.text = "Enviando notificación..."

        api.enviarNotificacionConImagen(
            textoPlano(titulo),
            textoPlano(mensaje),
            idDestinatario,
            parteImagen
        ).enqueue(object : Callback<JsonObject> {

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
                    imagenUri = null
                    txtImagenElegida.text = "Sin imagen adjunta"
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
                estado.text = "Error de conexión al enviar: ${t.message}"
            }
        })
    }

    private fun responderA(notificacion: Notificacion) {
        val posicion = destinatarios.indexOfFirst {
            it.id_usuario == notificacion.id_remitente
        }
        if (posicion < 0) {
            estado.text = "No puedes responder a este remitente."
            return
        }
        spnDestinatario.setSelection(posicion)
        campoTitulo.setText("Re: ${notificacion.titulo}".take(150))
        campoMensaje.requestFocus()
        estado.text = "Escribe tu respuesta y pulsa Enviar."
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

                        val urlImagen = notificacion.imagen_url
                        if (!urlImagen.isNullOrBlank()) {
                            tarjeta.addView(
                                TextView(this@NotificacionesActivity).apply {
                                    text = "Ver imagen adjunta"
                                    textSize = 15f
                                    setTextColor(0xFF1565C0.toInt())
                                    setPadding(0, 8, 0, 8)
                                    setOnClickListener {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(urlImagen)
                                            )
                                        )
                                    }
                                }
                            )
                        }

                        tarjeta.addView(fecha)

                        if (esSupervisor && notificacion.id_remitente != null) {
                            tarjeta.addView(
                                Button(this@NotificacionesActivity).apply {
                                    text = "Responder"
                                    setOnClickListener {
                                        responderA(notificacion)
                                    }
                                }
                            )
                        }

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
                    estado.text = "Error de conexión: ${t.message}"
                }
            })
    }
}
