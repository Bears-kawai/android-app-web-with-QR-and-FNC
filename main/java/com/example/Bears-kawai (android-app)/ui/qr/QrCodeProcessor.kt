package com.example.Bears-kawai.ui.qr

import com.example.Bears-kawai.MainActivity
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode

class QrCodeProcessor(
    private val activity: QrFragment,
    private val mainActivity: MainActivity
) : Detector.Processor<Barcode> {

    override fun release() {
        // Release resources if needed
    }

    override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        val barcodes = detections.detectedItems
        if (barcodes.size() > 0) {
            val qrCode = barcodes.valueAt(0).displayValue.trim()

            if(qrCode=="opciones"){
                mainActivity.switchToOptions()
                activity.closeQrFragment()
            } else {
                if (mainActivity.analizarURL(qrCode)) {
                    mainActivity.homeViewModel.qrCodeUrl = qrCode
                } else {
                    mainActivity.homeViewModel.qrCodeUrl = "Error"
                }

                mainActivity.switchToHomeOption()
                activity.closeQrFragment()
            }
        }
    }
}