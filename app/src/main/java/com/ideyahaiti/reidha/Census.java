package com.ideyahaiti.reidha;

import android.os.Parcel;
import android.os.Parcelable;

public class Census implements Parcelable {
    private int id;
    private String lastname;
    private String firstname;
    private String cin;
    private String birthdate;
    private String nif;
    private String address;
    private String phone;
    private int gender_id;
    private int status_id;
    private int agent_id;

    public Census() {
    }

    protected Census(Parcel in) {
        id = in.readInt();
        lastname = in.readString();
        firstname = in.readString();
        cin = in.readString();
        birthdate = in.readString();
        nif = in.readString();
        address = in.readString();
        phone = in.readString();
        gender_id = in.readInt();
        status_id = in.readInt();
        agent_id = in.readInt();
    }

    public static final Creator<Census> CREATOR = new Creator<Census>() {
        @Override
        public Census createFromParcel(Parcel in) {
            return new Census(in);
        }

        @Override
        public Census[] newArray(int size) {
            return new Census[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getGender_id() {
        return gender_id;
    }

    public void setGender_id(int gender_id) {
        this.gender_id = gender_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(lastname);
        dest.writeString(firstname);
        dest.writeString(cin);
        dest.writeString(birthdate);
        dest.writeString(nif);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeInt(gender_id);
        dest.writeInt(status_id);
        dest.writeInt(agent_id);
    }



}
