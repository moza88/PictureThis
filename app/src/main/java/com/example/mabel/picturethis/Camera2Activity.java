package com.example.mabel.picturethis;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.pubnub.api.vendor.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Camera2Activity extends AppCompatActivity {

    private String TAG = this.getClass().getName();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView buckysImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button buckyButton = (Button) findViewById(R.id.buckysButton);
        buckysImageView = (ImageView) findViewById(R.id.buckysImageView);

        //Disable the button if the user has no camera
        if(!hasCamera())
            buckyButton.setEnabled(false);
    }

    //Check if the user has a camera
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    //Launching the camera
    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Take a picture and pass results along to onActivityResult
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    //If you want to return the image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Get the photo
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            buckysImageView.setImageBitmap(photo);

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 90, stream);
            byte[] image=stream.toByteArray();
            System.out.println("byte array:"+image);

            String photo_base64 = Base64.encodeToString(image, 0);
            Log.d(TAG, "photo_base64: "+photo_base64);
            System.out.println("photo_base64: "+photo_base64);
        }
    }
}