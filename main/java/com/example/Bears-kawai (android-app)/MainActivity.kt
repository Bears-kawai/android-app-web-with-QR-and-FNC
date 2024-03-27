package com.example.Bears-kawai

import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.Bears-kawai.databinding.ActivityMainBinding
import com.example.Bears-kawai.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.scottyab.rootbeer.RootBeer
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Adapta el contenido pantalla completa sin bordes
        enableEdgeToEdge()

        // Esconde el menú superior
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rootBeer = RootBeer(this)

        if (rootBeer.isRooted) {
            // La aplicación se está ejecutando en un dispositivo rooteado
            Toast.makeText(this, "La aplicación no puede ejecutarse en un dispositivo rooteado.", Toast.LENGTH_SHORT).show()
            finish()
        } else if (isEmulator()) {
            // La aplicación se está ejecutando en un emulador, realiza las acciones correspondientes
            Toast.makeText(this, "La aplicación no puede ejecutarse en un emulador.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top-level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_qr
                )
            )

            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    // Cambia de fragmento home
    fun switchToHomeOption() {
        runOnUiThread {
            binding.navView.selectedItemId = R.id.navigation_home
        }
    }

    // Cambia de fragmento options
    fun switchToOptions() {
        runOnUiThread {
            binding.navView.selectedItemId = R.id.navigation_options
        }
    }

    fun analizarURL(url: String): Boolean {
        val enlace = url.takeIf { it.isNotEmpty() }?.let {
            if (!it.startsWith("https://")) {
                "https://$it"
            } else {
                it
            }
        }

        val perteneceAlDominio = enlace?.let {
            try {
                val uriEnlace = URL(it)
                val uriInicial = URL(homeViewModel.inicialUrl)

                // Comparar las partes relevantes de la URL (IP y ruta)
                val baseEnlace = uriEnlace.host
                val baseInicial = uriInicial.host

                baseEnlace == baseInicial
            } catch (e: MalformedURLException) {
                Log.e("Error al analizar la URL", e.message ?: "")
                false
            }
        } ?: false

        return perteneceAlDominio
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Debug.isDebuggerConnected()
                || isRunningInEmulatorByCpuInfo())
    }

    private fun isRunningInEmulatorByCpuInfo(): Boolean {
        try {
            val abi = Build.SUPPORTED_ABIS[0]
            if (abi.contains("x86") || abi.contains("x86_64")) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}