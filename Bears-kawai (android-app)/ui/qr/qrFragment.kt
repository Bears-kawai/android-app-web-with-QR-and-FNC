package com.example.Bears-kawai.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.Bears-kawai.MainActivity
import com.example.Bears-kawai.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class QrFragment : Fragment() {

    private lateinit var cameraSource: CameraSource
    private lateinit var surfaceView: SurfaceView
    private val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surfaceView = view.findViewById(R.id.surfaceView)

        val barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        if (!barcodeDetector.isOperational) {
            Toast.makeText(
                requireContext(),
                "No se puede establecer la detección de códigos QR. Inténtelo de nuevo más tarde.",
                Toast.LENGTH_SHORT
            ).show()
            activity?.onBackPressed()
        } else {
            val qrCodeProcessor = QrCodeProcessor(
                this,
                mainActivity
            )
            barcodeDetector.setProcessor(qrCodeProcessor)
        }

        cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
            .setAutoFocusEnabled(false)
            .build()

        // Iniciar la vista del escáner cuando la vista esté creada
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                startCameraSource()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
    }

    private fun startCameraSource() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                cameraSource.start(surfaceView.holder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    @Deprecated("Deprecated in Java")
    fun registerForActivityResult (
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraSource()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Se requiere permiso de la cámara para escanear códigos QR.",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
        cameraSource.release()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    fun closeQrFragment() {
        val fragmentManager: FragmentManager? = fragmentManager

        fragmentManager?.beginTransaction()?.remove(this)?.commit()
    }
}