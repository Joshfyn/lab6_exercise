package com.example.joshuaadeegbe.lab6;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private final int RC_PICTURE_TAKEN = 1111;
    private final int RC_PERMISSIONS = 2222;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main2Activity();
            }
        });*/

        if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, RC_PERMISSIONS);

            finish();
        }

    }

    /*public void Main2Activity() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // when the user clicks the button… {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, RC_PICTURE_TAKEN);
    }

    // this method gets called when you return from the camera application, with the picture included within
    // the data object
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICTURE_TAKEN && resultCode == RESULT_OK) {
        // the newly taken photo is now stored in a Bitmap object
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //… handle the facial recognition etc. here
        }
        if (requestCode == RC_PERMISSIONS && resultCode == RESULT_OK) {
            // restart the activity if you arrive here from the permission dialog
            Intent reboot = new Intent(this, MainActivity.class);
            startActivity(reboot);
        }
    }
}


