package com.example.aloha.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Friend implements Serializable, Parcelable {
    public String id, senderId, receiverId, senderEmail, receiverEmail, senderName, receiverName, senderImage, receiverImage, dateTime;
    public long friendConnect, status;
    public Date dateObject;

    public Friend() {
    }

    public Friend(String id, String senderId, String receiverId, String senderEmail, String receiverEmail, String senderName, String receiverName, String senderImage, String receiverImage, String dateTime, long friendConnect, long status, Date dateObject) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.senderImage = senderImage;
        this.receiverImage = receiverImage;
        this.dateTime = dateTime;
        this.friendConnect = friendConnect;
        this.status = status;
        this.dateObject = dateObject;
    }

    protected Friend(Parcel in) {
        id = in.readString();
        senderId = in.readString();
        receiverId = in.readString();
        senderEmail = in.readString();
        receiverEmail = in.readString();
        senderName = in.readString();
        receiverName = in.readString();
        senderImage = in.readString();
        receiverImage = in.readString();
        dateTime = in.readString();
        friendConnect = in.readLong();
        status = in.readLong();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getFriendConnect() {
        return friendConnect;
    }

    public void setFriendConnect(long friendConnect) {
        this.friendConnect = friendConnect;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", senderImage='" + senderImage + '\'' +
                ", receiverImage='" + receiverImage + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", friendConnect=" + friendConnect +
                ", status=" + status +
                ", dateObject=" + dateObject +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(senderId);
        parcel.writeString(receiverId);
        parcel.writeString(senderEmail);
        parcel.writeString(receiverEmail);
        parcel.writeString(senderName);
        parcel.writeString(receiverName);
        parcel.writeString(senderImage);
        parcel.writeString(receiverImage);
        parcel.writeString(dateTime);
        parcel.writeLong(friendConnect);
        parcel.writeLong(status);
    }
}
