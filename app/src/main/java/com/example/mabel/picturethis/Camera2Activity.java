package com.example.mabel.picturethis;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mabel.picturethis.pubsub.PubSubListAdapter;
import com.example.mabel.picturethis.pubsub.PubSubPnCallback;
import com.example.mabel.picturethis.util.Constants;
import com.example.mabel.picturethis.util.DateTimeUtil;
import com.example.mabel.picturethis.util.JsonUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.collect.ImmutableMap;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.vendor.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Camera2Activity extends AppCompatActivity {

    private String TAG = this.getClass().getName();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView buckysImageView;

    //Creating 2 PubNub listener objects one for Messaging & Presence and another one for multi-plexing
    private PubNub mPubnub_Multi;
    private PubSubListAdapter mPubSub;
    private PubNub mPubnub_DataStream;
    private PubSubPnCallback mPubSubPnCallback;
    private String mUsername;
    private SharedPreferences mSharedPrefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button buckyButton = (Button) findViewById(R.id.buckysButton);
        buckysImageView = (ImageView) findViewById(R.id.buckysImageView);

        //Disable the button if the user has no camera
        if(!hasCamera())
            buckyButton.setEnabled(false);

        this.mPubSub = new PubSubListAdapter(this);
        this.mPubSubPnCallback = new PubSubPnCallback(this.mPubSub);

        mSharedPrefs = getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE);

        initPubNub();
        initChannels();
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
            if(extras.isEmpty()){
                Log.d(TAG, "a photo wasn't taken so it will cause a null pointer issue");
            }else {
                Bitmap photo = (Bitmap) extras.get("data");
                buckysImageView.setImageBitmap(photo);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] image = stream.toByteArray();
                System.out.println("byte array:" + image);

                String photo_base64 = Base64.encodeToString(image, 0);
            }
        }
    }


    public void publish(View view) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        final EditText mMessage = (EditText) Camera2Activity.this.findViewById(R.id.new_message);

        final Map<String, String> message = ImmutableMap.<String, String>of("sender", account.toString(), "message", mMessage.getText().toString(), "timestamp", DateTimeUtil.getTimeStampUtc());

        Camera2Activity.this.mPubnub_DataStream.publish().channel(Constants.PUBSUB_CHANNEL_NAME).message(message).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        try {
                            if (!status.isError()) {
                                mMessage.setText("");
                                Log.v(TAG, "publish(" + JsonUtil.asJson(result) + ")");
                            } else {
                                Log.v(TAG, "publishErr(" + JsonUtil.asJson(status) + ")");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    /*Initializing PubNub Configuration using GoogleSignIn account token*/
    private final void initPubNub() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        this.mPubnub_DataStream =
                new PubNub(new PNConfiguration()
                        .setPublishKey(Constants.PUBNUB_PUBLISH_KEY)
                        .setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY)
                        .setUuid(account.toString()).setSecure(true));

        this.mPubnub_Multi =
                new PubNub(new PNConfiguration()
                        .setPublishKey(Constants.PUBNUB_PUBLISH_KEY)
                        .setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY)
                        .setUuid(account.toString()).setSecure(true));
    }

    /*Creating a Channel for messaging (PubSub)*/
    public static final List<String> PUBSUB_CHANNEL = Arrays.asList(Constants.PUBSUB_CHANNEL_NAME);

    /*Subscribing to a Channel so we can receive messages*/
    private final void initChannels() {
        //Register the callback for messaging (pubsub)
        this.mPubnub_DataStream.addListener(this.mPubSubPnCallback);

        //Subscribing to the messaging (pubsub) channel
        this.mPubnub_DataStream.subscribe().channels(PUBSUB_CHANNEL).withPresence().execute();
    }
}