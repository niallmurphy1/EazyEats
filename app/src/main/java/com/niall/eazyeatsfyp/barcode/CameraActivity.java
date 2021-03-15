package com.niall.eazyeatsfyp.barcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.niall.eazyeatsfyp.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements ImageAnalysis.Analyzer{


    BarcodeScannerOptions options =
            new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E).build();
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TextView textView;

    private Button scanBtn;


    private Intent barcodeIntent;
    private String upcBarcode;
    public static final String UPC_BARCODE = "upcBarcode";

    boolean firstLaunch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        barcodeIntent = new Intent(this, BarcodeResultActivity.class);


        scanBtn = findViewById(R.id.scanFinBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanFinClick();
            }
        });

        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        textView = findViewById(R.id.orientation);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {

                try {


                    Log.d("TAG", "analyze: called");
                    BarcodeScanner client = BarcodeScanning.getClient(options);
                    @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = image.getImage();
                    if (mediaImage != null) {
                        InputImage inputImage =
                                InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
                        // Pass image to an ML Kit Vision API
                        client.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                            @Override
                            public void onSuccess(List<Barcode> barcodes) {
                                if (barcodes != null && !barcodes.isEmpty()) {


                                    textView.setText(barcodes.get(0).getDisplayValue());

                                    for (Barcode barcode : barcodes) {

                                        // if barcode is valid (Regex), open new barcode search activity, close loop and finish activity
                                        Log.d("TAG", "These are the barcodes: " + barcode.getDisplayValue());

                                        if(barcodes.size() == 1){

                                            firstLaunch = true;
                                            upcBarcode = barcodes.get(0).getDisplayValue();


                                            break;

                                        }
                                        else{
                                            firstLaunch = false;
                                        }

                                    }

//                                    if(firstLaunch){
//                                    barcodeIntent.putExtra(UPC_BARCODE, upcBarcode);
//                                    startActivity(barcodeIntent);
//                                    finish();
//                                    }


                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("FAIL", "onFailure: " + e);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Barcode>> task) {
                                image.close();
                            }
                        });
                        // ...
                    } else {
                        image.close();

                    }
                } catch (Exception exception) {
                    Log.d("TAG", "analyze: " + exception);
                }

            }
        });
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                //textView.setText(Integer.toString(orientation));
            }
        };
        orientationEventListener.enable();
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,
                imageAnalysis, preview);
    }

    public void onScanFinClick(){

        barcodeIntent.putExtra(UPC_BARCODE, upcBarcode);
        startActivity(barcodeIntent);
        finish();
    }


    @Override
    public void analyze(@NonNull ImageProxy image) {

    }
}