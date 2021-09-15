package com.appsxone.facedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsxone.facedetection.helper.GraphicOverlay;
import com.appsxone.facedetection.helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListenerAdapter;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;


public class MainActivity extends AppCompatActivity {
    Button faceDetectButton;
    GraphicOverlay graphicOverlay;
    CameraView cameraView;
    AlertDialog alertDialog;
    TextView tvResult;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.cameraView);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        faceDetectButton = findViewById(R.id.detect_face_btn);
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait, Processing...")
                .setCancelable(false)
                .build();

        tvResult = findViewById(R.id.tvResult);
        image = findViewById(R.id.image);

        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();*/

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 123);

            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListenerAdapter() {
            @Override
            public void onEvent(CameraKitEvent event) {
                super.onEvent(event);
            }

            @Override
            public void onError(CameraKitError error) {
                super.onError(error);
            }

            @Override
            public void onImage(CameraKitImage image) {
                super.onImage(image);
                alertDialog.show();
                Bitmap bitmap = image.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                processFaceDetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo video) {
                super.onVideo(video);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);
            alertDialog.show();
            processFaceDetection(photo);
        }
    }

    private void processFaceDetection(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance().
                getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                getFaceReults(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tvResult.setText(e.getMessage());
            }
        });
    }

    private void getFaceReults(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter = 0;
        for (FirebaseVisionFace face : firebaseVisionFaces) {
            Rect rect = face.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
            graphicOverlay.add(rectOverlay);
            counter = counter + 1;
        }

        if (counter == 0) {
            tvResult.setText("No Face To Detect");
        } else if (counter == 1) {
            tvResult.setText(counter + " Face Detected");
        } else if (counter > 1) {
            tvResult.setText(counter + " Faces Detected");
        }

        alertDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        cameraView.start();
    }
}