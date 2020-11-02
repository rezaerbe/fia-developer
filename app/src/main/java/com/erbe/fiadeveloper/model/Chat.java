package com.erbe.fiadeveloper.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Chat {

    private String mName;
    private String mMessage;
    private String mUid;
    private Date mTimestamp;

    public Chat() {
    }

    public Chat(String mName, String mMessage, String mUid) {
        this.mName = mName;
        this.mMessage = mMessage;
        this.mUid = mUid;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    @ServerTimestamp
    public Date getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(Date mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chat chat = (Chat) o;

        return mTimestamp.equals(chat.mTimestamp)
                && mUid.equals(chat.mUid)
                && (mName == null ? chat.mName == null : mName.equals(chat.mName))
                && (mMessage == null ? chat.mMessage == null : mMessage.equals(chat.mMessage));
    }

    public int hashCode() {
        int result = mName == null ? 0 : mName.hashCode();
        result = 31 * result + (mMessage == null ? 0 : mMessage.hashCode());
        result = 31 * result + mUid.hashCode();
        result = 31 * result + mTimestamp.hashCode();
        return result;
    }

    @NonNull
    public String toString() {
        return "Chat{" +
                "mName='" + mName + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mUid='" + mUid + '\'' +
                ", mTimestamp=" + mTimestamp +
                '}';
    }
}
