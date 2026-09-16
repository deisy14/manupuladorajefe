package com.example.manipuladoraj.api

import com.example.manipuladoraj.model.Ingrediente
import com.example.manipuladoraj.model.UsuarioTurno
import com.example.manipuladoraj.model.Turno
import com.example.manipuladoraj.model.RecetaComponente
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("ingredientes/")
    fun obtenerIngredientes(): Call<List<Ingrediente>>

    @GET("usuario-turno/")
    fun obtenerUsuarioTurnos(): Call<List<UsuarioTurno>>

    @GET("turnos/")
    fun obtenerTurnos(): Call<List<Turno>>

    @GET("receta-componentes/")
    fun obtenerRecetaComponentes(): Call<List<RecetaComponente>>
}
