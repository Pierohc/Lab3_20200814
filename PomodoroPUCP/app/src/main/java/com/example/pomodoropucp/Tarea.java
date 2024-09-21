package com.example.pomodoropucp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Tarea implements Parcelable {

    private int id;

    @SerializedName("todo")
    private String tarea;

    @SerializedName("completed")
    private boolean completado;

    private int userId;

    protected Tarea(Parcel in) {
        id = in.readInt();
        tarea = in.readString();
        completado = in.readByte() != 0;
        userId = in.readInt();
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(tarea);
        dest.writeByte((byte) (completado ? 1 : 0));
        dest.writeInt(userId);
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", tarea='" + tarea + '\'' +
                ", completado=" + completado +
                ", userId=" + userId +
                '}';
    }
}
