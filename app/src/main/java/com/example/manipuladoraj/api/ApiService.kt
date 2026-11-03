package com.example.manipuladoraj.api

import com.example.manipuladoraj.model.Ingrediente
import com.example.manipuladoraj.model.DetallePlato
import com.example.manipuladoraj.model.Plato
import com.example.manipuladoraj.model.UsuarioTurno
import com.example.manipuladoraj.model.Turno
import com.example.manipuladoraj.model.RecetaComponente
import com.example.manipuladoraj.model.InventarioItem
import com.example.manipuladoraj.model.MenuPae
import com.example.manipuladoraj.model.Notificacion
import com.example.manipuladoraj.model.SugerenciaRecetaRequest
import com.example.manipuladoraj.model.SugerenciaRecetaResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class LoginRequest(
    val correo: String,
    val password: String
)

data class EnviarNotificacionRequest(
    val titulo: String,
    val mensaje: String
)

data class LoginResponse(
    val access: String,
    val refresh: String?,
    val usuario: com.google.gson.JsonObject?
)

data class Destinatario(
    val id_usuario: Int,
    val nombre: String = "",
    val rol: String = ""
)

interface ApiService {

    @POST("auth/login/")
    fun iniciarSesion(@Body datos: LoginRequest): Call<LoginResponse>

    @GET("ingredientes/")
    fun obtenerIngredientes(): Call<List<Ingrediente>>

    @GET("usuario-turno/")
    fun obtenerUsuarioTurnos(): Call<List<UsuarioTurno>>

    @GET("turnos/")
    fun obtenerTurnos(): Call<List<Turno>>

    @GET("receta-componentes/")
    fun obtenerRecetaComponentes(): Call<List<RecetaComponente>>

    @GET("inventario/")
    fun obtenerInventario(): Call<List<InventarioItem>>

    @GET("menus/")
    fun obtenerMenus(): Call<List<MenuPae>>

    @GET("detalle_plato/")
    fun obtenerDetallesPlato(): Call<List<DetallePlato>>

    @GET("platos/")
    fun obtenerPlatos(): Call<List<Plato>>

    @POST("ia/sugerencias-recetas/")
    fun sugerirReceta(
        @Body datos: SugerenciaRecetaRequest
    ): Call<SugerenciaRecetaResponse>

    @GET("notificaciones/")
    fun obtenerNotificaciones(): Call<List<Notificacion>>

    @POST("notificaciones/enviar/")
    fun enviarNotificacion(
        @Body datos: EnviarNotificacionRequest
    ): Call<com.google.gson.JsonObject>

    @Multipart
    @POST("notificaciones/enviar/")
    fun enviarNotificacionConImagen(
        @Part("titulo") titulo: RequestBody,
        @Part("mensaje") mensaje: RequestBody,
        @Part("id_destinatario") idDestinatario: RequestBody?,
        @Part imagen: MultipartBody.Part?
    ): Call<com.google.gson.JsonObject>

    @GET("notificaciones/destinatarios/")
    fun obtenerDestinatarios(): Call<List<Destinatario>>

    @POST("notificaciones/{id}/marcar-leida/")
    fun marcarNotificacionLeida(
        @Path("id") id: Int
    ): Call<com.google.gson.JsonObject>

    @POST("notificaciones/marcar-todas-leidas/")
    fun marcarTodasLeidas(): Call<com.google.gson.JsonObject>
}
