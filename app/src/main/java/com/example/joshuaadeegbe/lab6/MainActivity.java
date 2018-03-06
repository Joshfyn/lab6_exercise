package com.example.joshuaadeegbe.lab6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final int RC_PERMISSIONS = 2222;
    private final String ACTION_FACE_UPDATE = "ACTION_FACE_UPDATE";

    private SurfaceView cameraPreview;
    private SurfaceHolder cameraHolder;

    private CameraSource cameraSource;

    private TextView debug_text;

    private com.google.android.gms.vision.face.FaceDetector faceDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSIONS);
            finish();
            return;
        }

        cameraPreview = findViewById(R.id.cameraview);
        cameraHolder = cameraPreview.getHolder();
        cameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraHolder.setFixedSize(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);

        debug_text = findViewById(R.id.debug_text);

        /*button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main2Activity();
            }
        });*/

    }


    public void Main2Activity() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);

    }


    private UIUpdater uiUpdater = new UIUpdater();
    private class UIUpdater extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_FACE_UPDATE)) {
                debug_text.setText(intent.getStringExtra("faces"));
            }

            debug_text.invalidate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FACE_UPDATE);
        registerReceiver(uiUpdater, filter);

        final Button faces = findViewById(R.id.btn_faces);
        faces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Initialising face detection...", Toast.LENGTH_SHORT).show();

                if (cameraSource != null) cameraSource.stop();

                if (faceDetector == null) {
                    faceDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                            .setTrackingEnabled(true)
                            .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS) //eyes, smile
                            .build();
                }

                if (faceDetector.isOperational()) {
                    faceDetector.setProcessor(new LargestFaceFocusingProcessor(faceDetector, new FaceTracker()));
                    cameraSource = new CameraSource.Builder(getApplicationContext(), faceDetector)
                            .setFacing(CameraSource.CAMERA_FACING_FRONT)
                            .setRequestedFps(30)
                            .build();
                    try {
                        cameraSource.start(cameraHolder);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("oops", "Face detection not supported by this camera....");
                    Toast.makeText(getApplicationContext(), "Face detection not supported!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class FaceTracker extends Tracker<Face> {
        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);

            Intent faces = new Intent(ACTION_FACE_UPDATE);
            faces.putExtra("faces", detections.getDetectedItems().size());
            sendBroadcast(faces);

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
            if (face.getIsSmilingProbability()>=0){
                Main2Activity();
            }

            String debug = "Faces: " + detections.getDetectedItems().size() + "Smile:" + (face.getIsSmilingProbability());

            Intent faces = new Intent(ACTION_FACE_UPDATE);
            faces.putExtra("faces", debug);
            sendBroadcast(faces);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (uiUpdater != null) unregisterReceiver(uiUpdater);
        if (cameraSource != null) cameraSource.release();
        if (faceDetector != null) faceDetector.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PERMISSIONS && resultCode == RESULT_OK) {
            Intent reboot = new Intent(this, MainActivity.class);
            startActivity(reboot);
        }
    }
}


