package it.imp.lucenteCantieri.ui.barcode;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Administrator on 06/03/2017.
 */

public interface BarcodeListener {
    public void barcodeDetected(Barcode barcode);
}
