package com.example.mabel.picturethis.pubsub;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.example.mabel.picturethis.util.JsonUtil;

/*
Once the app completes the subscription, a callback is invoked for each new channel event that comes from PubNub.
 */
public class PubSubPnCallback extends SubscribeCallback {
    private static final String TAG = PubSubPnCallback.class.getName();
    private final PubSubListAdapter pubSubListAdapter;

    public PubSubPnCallback(PubSubListAdapter pubSubListAdapter) {
        this.pubSubListAdapter = pubSubListAdapter;
    }

    /*Tells us what to do if the user gets disconnected, etc. */
    @Override
    public void status(PubNub pubnub, PNStatus status) {
        /*
        switch (status.getCategory()) {
             // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
             case PNStatusCategory.PNConnectedCategory:
             case PNStatusCategory.PNUnexpectedDisconnectCategory:
             case PNStatusCategory.PNReconnectedCategory:
             case PNStatusCategory.PNDecryptionErrorCategory:
         }
        */

        // no status handling for simplicity
    }

    /*This converts incoming messages into a pojo so we can feed it into an adapter in android*/
    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        try {
            Log.v(TAG, "message(" + JsonUtil.asJson(message) + ")");

            JsonNode jsonMsg = message.getMessage();
            PubSubPojo dsMsg = JsonUtil.convert(jsonMsg, PubSubPojo.class);

            this.pubSubListAdapter.add(dsMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*We handle presence events over here*/
    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        // no presence handling for simplicity
    }
}
