package com.example.joshuaadeegbe.lab6;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

public class Main2Activity extends AppCompatActivity {

    private final int RC_PICTURE_TAKEN = 1111;
    private ImageView photoTaken;
    private FaceDetector faceDetector;
    private CameraSource cameraSource;
    private SurfaceHolder cameraHolder;
    private final String ACTION_FACE_UPDATE = "ACTION_FACE_UPDATE";
    public float faceSmile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (photoTaken == null) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, RC_PICTURE_TAKEN);
        }
        faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS) //eyes, smile
                .build();

        faceDetector.setProcessor(new LargestFaceFocusingProcessor(faceDetector, new FaceTracker()));
        cameraSource = new CameraSource.Builder(getApplicationContext(), faceDetector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30)
                .build();
        /*try {
            cameraSource.start(cameraHolder);
            } catch (SecurityException e) {
            e.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
        }*/

        faceDetector.release();



    }
    private class FaceTracker extends Tracker<Face> {
        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);


        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            super.onUpdate(detections, face);
            float smilepro = face.getIsSmilingProbability();
            String smileprob = String.valueOf(smilepro);

            Intent smiles = new Intent(ACTION_FACE_UPDATE);
            smiles.putExtra("smiles", smileprob);
            sendBroadcast(smiles);



        }

    }

    private UIUpdater uiUpdater = new UIUpdater();
    private class UIUpdater extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_FACE_UPDATE)) {
                String faceSmil = intent.getStringExtra("smiles");
                faceSmile = Float.valueOf(faceSmil.trim()).floatValue();

            }

        }
    }

    public void smile(){
        Intent intent = new Intent(this, Main3Activity.class);
        startActivity(intent);

    }

    public void notSmiling(){
        Intent intent = new Intent(this, Main4Activity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Unfortunately, you are not smiling", Toast.LENGTH_SHORT).show();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICTURE_TAKEN && resultCode == RESULT_OK) {
            if (faceSmile >= 0.8){
                smile();
            }
            else {
                notSmiling();
            }
            


        }
    }

}
