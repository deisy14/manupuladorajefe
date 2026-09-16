package com.example.manipuladoraj.model

data class Ingrediente(
    val id_ingrediente: Int,
    val id_categoria_inventario: Int?,
    val id_unidad_medida: Int?,
    val nombre_ingrediente: String?,
    val descripcion: String?,
    val imagen_ingrediente: String?,
    val marca_ingrediente: String?
)
