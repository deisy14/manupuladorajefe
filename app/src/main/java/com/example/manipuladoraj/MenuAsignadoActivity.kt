package com.example.manipuladoraj

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MenuAsignadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu_asignado)

        configurarBotonPreparaciones()
        configurarBarraInferior()
    }

    private fun configurarBotonPreparaciones() {

        findViewById<View>(R.id.btnVerPreparaciones).setOnClickListener {

            val intent = Intent(this, PreparacionesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarBarraInferior() {

        findViewById<View>(R.id.navInicio).setOnClickListener {
            startActivity(Intent(this, MenuDiarioActivity::class.java))
        }

        findViewById<View>(R.id.navInventario).setOnClickListener {
            startActivity(Intent(this, InventarioActivity::class.java))
        }

        findViewById<View>(R.id.navPreparaciones).setOnClickListener {
            startActivity(Intent(this, PreparacionesActivity::class.java))
        }

        findViewById<View>(R.id.navManipuladoras).setOnClickListener {
            startActivity(Intent(this, ManipuladorasActivity::class.java))
        }

        findViewById<View>(R.id.navPerfil).setOnClickListener {
            // Aquí irá el perfil
        }
    }
}