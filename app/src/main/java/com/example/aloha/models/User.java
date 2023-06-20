package com.example.aloha.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable, Parcelable {
    public String name,email,image,token,id;
    public long availability;
    public int status;

    public User() {
    }

    public User(String name, String email, String image, String token, String id, long availability, int status) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.token = token;
        this.id = id;
        this.availability = availability;
        this.status = status;
    }

    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        image = in.readString();
        token = in.readString();
        id = in.readString();
        availability = in.readLong();
        status = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAvailability() {
        return availability;
    }

    public void setAvailability(long availability) {
        this.availability = availability;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", token='" + token + '\'' +
                ", id='" + id + '\'' +
                ", availability=" + availability +
                ", status=" + status +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(image);
        parcel.writeString(token);
        parcel.writeString(id);
        parcel.writeLong(availability);
        parcel.writeInt(status);
    }
}
