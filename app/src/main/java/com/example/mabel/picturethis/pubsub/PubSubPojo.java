package com.example.mabel.picturethis.pubsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.android.gms.common.internal.Objects.equal;

public class PubSubPojo {
    private final String sender;
    private final String message;
    private final String timestamp;
    private final String photo_base64;

    public PubSubPojo(
            @JsonProperty("sender") String sender,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("photo_base64") String photo_base64) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.photo_base64 = photo_base64;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPhoto_base64() { return photo_base64; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PubSubPojo other = (PubSubPojo) obj;

        return Objects.equal(this.sender, other.sender)
                && Objects.equal(this.message, other.message)
                && Objects.equal(this.timestamp, other.timestamp)
                && Objects.equal(this.photo_base64, other.photo_base64);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, message, timestamp, photo_base64);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(PubSubPojo.class)
                .add("sender", sender)
                .add("message", message)
                .add("timestamp", timestamp)
                .add("photo_base64", photo_base64)
                .toString();
    }
}
