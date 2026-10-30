package com.example.manipuladoraj.model

data class Notificacion(
    val id_notificacion: Int,
    val id_usuario: Int? = null,
    val id_remitente: Int? = null,
    val titulo: String = "",
    val mensaje: String = "",
    val imagen_url: String? = null,
    val fecha_hora: String? = null,
    val leida: Boolean = false
)
