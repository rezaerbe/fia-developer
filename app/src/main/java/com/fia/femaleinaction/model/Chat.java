package com.fia.femaleinaction.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Chat {
    private String mName;
    private String mMessage;
    private String mUid;
    private Date mTimestamp;

    public Chat() {
        // Needed for Firebase
    }

    public Chat(@Nullable String name, @Nullable String message, @NonNull String uid) {
        mName = name;
        mMessage = message;
        mUid = uid;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public void setName(@Nullable String name) {
        mName = name;
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }

    public void setMessage(@Nullable String message) {
        mMessage = message;
    }

    @NonNull
    public String getUid() {
        return mUid;
    }

    public void setUid(@NonNull String uid) {
        mUid = uid;
    }

    @ServerTimestamp
    @Nullable
    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(@Nullable Date timestamp) {
        mTimestamp = timestamp;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chat chat = (Chat) o;

        return mTimestamp.equals(chat.mTimestamp)
                && mUid.equals(chat.mUid)
                && (mName == null ? chat.mName == null : mName.equals(chat.mName))
                && (mMessage == null ? chat.mMessage == null : mMessage.equals(chat.mMessage));
    }

    @Override
    public int hashCode() {
        int result = mName == null ? 0 : mName.hashCode();
        result = 31 * result + (mMessage == null ? 0 : mMessage.hashCode());
        result = 31 * result + mUid.hashCode();
        result = 31 * result + mTimestamp.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Chat{" +
                "mName='" + mName + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mUid='" + mUid + '\'' +
                ", mTimestamp=" + mTimestamp +
                '}';
    }
}