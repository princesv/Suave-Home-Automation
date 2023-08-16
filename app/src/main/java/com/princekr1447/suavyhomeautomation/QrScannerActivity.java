package com.princekr1447.suavyhomeautomation;


import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class QrScannerActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final String TAG = "QR_CODE_SCANNER";
    static final String TAG_URI_TYPE = "uri-type";
    static final int PICK_IMAGE=11;
    private BarcodeDetector barcodeDetector;
    /* access modifiers changed from: private */
    public CameraSource cameraSource;
    String intentData = "";
    SurfaceView surfaceView;
    private Uri imageUri;
    boolean flashState=false;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_qr_scanner);
        initComponents();
    }

    private void initComponents() {
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        initDialog();
    }
    public void initDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_qr_activity);
        dialog.setCanceledOnTouchOutside(false);
        ImageView importFromGalleryBtn=dialog.findViewById(R.id.importFromGalleryBtn);
        ImageView enterKeyManuallyBtn=dialog.findViewById(R.id.enterKeyManuallyBtn);
        importFromGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeBarcodePicture();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        final EditText enterKeyManuallyET=dialog.findViewById(R.id.enterKeyManuallyET);
        enterKeyManuallyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prodKeyaTxt=enterKeyManuallyET.getText().toString().trim();
                if(prodKeyaTxt.isEmpty()){
                    enterKeyManuallyET.requestFocus();
                    enterKeyManuallyET.setError("Text field empty");
                    return;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",prodKeyaTxt);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
                    QrScannerActivity.this.setBarCodeCamera(barCode);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void openCamera() {
        try {
            this.cameraSource.start(this.surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /* access modifiers changed from: private */
    public void setBarCodeCamera(SparseArray<Barcode> barCode) {
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
    private void takeBarcodePicture() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScanIntent.setData(this.imageUri);
        sendBroadcast(mediaScanIntent);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == -1) {
            launchMediaScanIntent();
            try {
                this.imageUri = data.getData();
                Bitmap bitmap = decodeBitmapUri(this, this.imageUri);
                if (!this.barcodeDetector.isOperational() || bitmap == null) {
                    Toast.makeText(this, "Detector initialization failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                setBarCode(this.barcodeDetector.detect(new Frame.Builder().setBitmap(bitmap).build()));
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        }
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), (Rect) null, bmOptions);
        int scaleFactor = Math.min(bmOptions.outWidth / 600, bmOptions.outHeight / 600);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), (Rect) null, bmOptions);
    }

    private void setBarCode(SparseArray<Barcode> barCodes) {
        if (barCodes.size() == 0) {
            Toast.makeText(this, "No barcode detected.", Toast.LENGTH_SHORT).show();
            return;
        }
        Barcode code = barCodes.valueAt(0);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",code.rawValue);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        switch (barCodes.valueAt(0).valueFormat) {
            case 1:
                Log.i(TAG, code.contactInfo.title);
                return;
            case 2:
                Log.i(TAG, code.displayValue);
                return;
            case 3:
                Log.i(TAG, code.rawValue);
                return;
            case 4:
                Log.i(TAG, code.phone.number);
                return;
            case 5:
                Log.i(TAG, code.rawValue);
                return;
            case 6:
                Log.i(TAG, code.sms.message);
                return;
            case 7:
                Log.i(TAG, code.displayValue);
                return;
            case 8:
                Log.i(TAG, "url: " + code.displayValue);
                return;
            case 9:
                Log.i(TAG, code.wifi.ssid);
                return;
            case 10:
                Log.i(TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                return;
            case 11:
                Log.i(TAG, code.calendarEvent.description);
                return;
            case 12:
                Log.i(TAG, code.driverLicense.licenseNumber);
                return;
            default:
                Log.i(TAG, code.rawValue);
                return;
        }
    }
    void flashLightStateChange(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                flashState=!flashState;
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, flashState);   //Turn ON
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }



    }
}
