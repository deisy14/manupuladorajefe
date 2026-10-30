
package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manipuladoraj.api.LoginRequest
import com.example.manipuladoraj.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var progresoLogin: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencias = getSharedPreferences(
            "user_prefs",
            MODE_PRIVATE
        )

        // Si ya existe un token, entrar al menú.
        val token = preferencias.getString("auth_token", null)

        if (!token.isNullOrBlank()) {
            abrirMenu()
            return
        }

        setContentView(R.layout.activity_main)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        progresoLogin = findViewById(R.id.progresoLogin)

        btnIniciarSesion.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString()

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Ingresa tu correo y contraseña",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        btnIniciarSesion.isEnabled = false
        progresoLogin.visibility = View.VISIBLE

        RetrofitClient.getApiService(this)
            .iniciarSesion(LoginRequest(correo, password))
            .enqueue(object :
                Callback<com.example.manipuladoraj.api.LoginResponse> {

                override fun onResponse(
                    call: Call<com.example.manipuladoraj.api.LoginResponse>,
                    response: Response<com.example.manipuladoraj.api.LoginResponse>
                ) {
                    btnIniciarSesion.isEnabled = true
                    progresoLogin.visibility = View.GONE

                    val resultado = response.body()

                    if (
                        response.isSuccessful &&
                        resultado != null &&
                        resultado.access.isNotBlank()
                    ) {
                        val usuario = resultado.usuario

                        val rol = usuario
                            ?.get("rol")
                            ?.takeIf { !it.isJsonNull }
                            ?.asString

                        val idUsuario = usuario
                            ?.get("id_usuario")
                            ?.takeIf { !it.isJsonNull }
                            ?.asInt ?: -1

                        getSharedPreferences(
                            "user_prefs",
                            MODE_PRIVATE
                        ).edit()
                            .putString("auth_token", resultado.access)
                            .putString("refresh_token", resultado.refresh)
                            .putString("rol", rol)
                            .putInt("id_usuario", idUsuario)
                            .apply()

                        Toast.makeText(
                            this@MainActivity,
                            "Inicio de sesión correcto",
                            Toast.LENGTH_SHORT
                        ).show()

                        abrirMenu()

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "No se pudo iniciar sesión. " +
                                "Verifica tus datos. Código: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.manipuladoraj.api.LoginResponse>,
                    t: Throwable
                ) {
                    btnIniciarSesion.isEnabled = true
                    progresoLogin.visibility = View.GONE

                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun abrirMenu() {
        val intent = Intent(
            this,
            MenuDiarioActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}
