package com.princekr1447.suavyhomeautomation;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;

public class QrScannerActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final String TAG = "QR_CODE_SCANNER";
    static final String TAG_URI_TYPE = "uri-type";
    private BarcodeDetector barcodeDetector;
    /* access modifiers changed from: private */
    public CameraSource cameraSource;
    String intentData = "";
    SurfaceView surfaceView;
    TextView textViewBarCodeValue;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_qr_scanner);
        initComponents();
    }

    private void initComponents() {
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
    }

    private void initialiseDetectorsAndSources() {
        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        this.barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(0).build();
        this.cameraSource = new CameraSource.Builder(this, this.barcodeDetector).setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true).build();
        this.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                QrScannerActivity.this.openCamera();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                QrScannerActivity.this.cameraSource.stop();
            }
        });
        this.barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            public void release() {
                Toast.makeText(QrScannerActivity.this.getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barCode = detections.getDetectedItems();
                if (barCode.size() > 0) {
                    QrScannerActivity.this.setBarCode(barCode);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0) {
                this.cameraSource.start(this.surfaceView.getHolder());
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, REQUEST_CAMERA_PERMISSION);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void setBarCode(SparseArray<Barcode> barCode) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",barCode.valueAt(0).rawValue);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.cameraSource.release();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void copyToClipBoard(String text) {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("QR code Scanner", text));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CAMERA_PERMISSION || grantResults.length <= 0) {
            finish();
        } else if (grantResults[0] == -1) {
            finish();
        } else {
            openCamera();
        }
    }
}
