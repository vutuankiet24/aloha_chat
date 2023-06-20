package com.example.aloha.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable, Parcelable {
    public String senderId, receiverId, message, dateTime;
    public Date dateObject;
    public String conversionId, conversionName, conversionImage, lastMessageUser, lastImageUser;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, String dateTime, Date dateObject, String conversionId, String conversionName, String conversionImage, String lastMessageUser, String lastImageUser) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
        this.dateObject = dateObject;
        this.conversionId = conversionId;
        this.conversionName = conversionName;
        this.conversionImage = conversionImage;
        this.lastMessageUser = lastMessageUser;
        this.lastImageUser = lastImageUser;
    }

    protected ChatMessage(Parcel in) {
        senderId = in.readString();
        receiverId = in.readString();
        message = in.readString();
        dateTime = in.readString();
        conversionId = in.readString();
        conversionName = in.readString();
        conversionImage = in.readString();
        lastMessageUser = in.readString();
        lastImageUser = in.readString();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getConversionName() {
        return conversionName;
    }

    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public String getConversionImage() {
        return conversionImage;
    }

    public void setConversionImage(String conversionImage) {
        this.conversionImage = conversionImage;
    }

    public String getLastMessageUser() {
        return lastMessageUser;
    }

    public void setLastMessageUser(String lastMessageUser) {
        this.lastMessageUser = lastMessageUser;
    }

    public String getLastImageUser() {
        return lastImageUser;
    }

    public void setLastImageUser(String lastImageUser) {
        this.lastImageUser = lastImageUser;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", dateObject=" + dateObject +
                ", conversionId='" + conversionId + '\'' +
                ", conversionName='" + conversionName + '\'' +
                ", conversionImage='" + conversionImage + '\'' +
                ", lastMessageUser='" + lastMessageUser + '\'' +
                ", lastImageUser='" + lastImageUser + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(senderId);
        parcel.writeString(receiverId);
        parcel.writeString(message);
        parcel.writeString(dateTime);
        parcel.writeString(conversionId);
        parcel.writeString(conversionName);
        parcel.writeString(conversionImage);
        parcel.writeString(lastMessageUser);
        parcel.writeString(lastImageUser);
    }
}
