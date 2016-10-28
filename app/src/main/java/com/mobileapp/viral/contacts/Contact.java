package com.mobileapp.viral.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for Contact object that contains the relevant variables for name, phone
 * number, relationships, and checkbox state. Parcelable was used to send
 * Contact objects between activities.
 */
public class Contact implements Parcelable {
    private String name;
    private String phone;
    private ArrayList<Contact> relationships = new ArrayList<Contact>();
    private boolean checkBoxState;

    public Contact() {}

    //default constructor
    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isChecked() {
        return checkBoxState;
    }

    public void setChecked(boolean status) {
        this.checkBoxState = status;
    }

    public ArrayList<Contact> getRelationships() {
        return relationships;
    }

    public Contact(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        this.name = data[0];
        this.phone = data[1];
        in.readTypedList(relationships, Contact.CREATOR);
    }

    //need to override equals method so removing a contact from ArrayList works properly
    @Override
    public boolean equals(Object o)
    {
        if(o.getClass() != this.getClass() || o == null) {
            return false;
        }
        else {
            return ((Contact) o).getName().equals(this.getName());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {name, phone});
        dest.writeTypedList(relationships);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
