package com.example.Bears-kawai.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.Bears-kawai.MainActivity
import com.example.Bears-kawai.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class OptionsFragment : Fragment() {

    private lateinit var editTextVariableUrl: EditText
    private lateinit var editTextVariableName: EditText
    private lateinit var editTextVariablePass: EditText
    private lateinit var btnGuardar: Button

    // Puedes inicializar tus variables con valores predeterminados aquí
    private var variableUrl: String = ""
    private var variableName: String = ""
    private var variablePass: String = ""

    private val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_options, container, false)

        editTextVariableUrl = view.findViewById(R.id.editTextUrl)
        editTextVariableName = view.findViewById(R.id.editTextUser)
        editTextVariablePass = view.findViewById(R.id.editTextPass)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        // Manejar el clic del botón Guardar
        btnGuardar.setOnClickListener {

            // Guardar los nuevos valores de las variables

            if (editTextVariableUrl.text.toString().isNotEmpty()) {
                variableUrl = "https://"+editTextVariableUrl.text.toString().trim()+"/"

                mainActivity.homeViewModel.inicialUrl = variableUrl
                mainActivity.homeViewModel.currentUrl = variableUrl
            }


            if(editTextVariableName.text.toString().isNotEmpty()){
                variableName = editTextVariableName.text.toString()
                mainActivity.homeViewModel.username=variableName
            }
            if(editTextVariablePass.text.toString().isNotEmpty()){
                variablePass = editTextVariablePass.text.toString()
                mainActivity.homeViewModel.pass=variablePass
            }

            Toast.makeText(requireContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show()

            mainActivity.switchToHomeOption()
        }

        val btnMostrarTexto: Button = view.findViewById(R.id.btnMostrarTexto)
        val textoAMostrar = view.findViewById<TextView>(R.id.textoAMostrar)

        btnMostrarTexto.setOnClickListener {
            // Cambiar la visibilidad del contenedor de texto al contrario de su estado actual
            textoAMostrar.visibility = if (textoAMostrar.visibility == View.VISIBLE) View.GONE else View.VISIBLE

            // Si se está mostrando el texto, cargar el contenido del archivo
            if (textoAMostrar.visibility == View.VISIBLE) {
                try {
                    // Abrir el archivo desde el directorio assets
                    val inputStream: InputStream = requireContext().assets.open("license.txt")
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val contenido = StringBuilder()
                    var linea: String?

                    // Leer el contenido del archivo línea por línea
                    while (reader.readLine().also { linea = it } != null) {
                        contenido.append(linea).append('\n')
                    }

                    // Cerrar el lector
                    reader.close()

                    // Establecer el contenido del TextView
                    textoAMostrar.text = contenido.toString()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return view
    }
}