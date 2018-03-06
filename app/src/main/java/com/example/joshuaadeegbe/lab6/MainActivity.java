package com.example.joshuaadeegbe.lab6;


import android.content.Intent;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button button;

    private com.google.android.gms.vision.face.FaceDetector faceDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = (Button) findViewById(R.id.btn_faces);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main2Activity();
            }
        });

    }


    public void Main2Activity() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);

    }

}


