package com.mobileapp.viral.contacts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobileapp.viral.contacts.Contact;
import com.mobileapp.viral.contacts.R;

import java.util.ArrayList;

/**
 * Adapter class for Profile activity
 */
public class ProfileAdapter extends ArrayAdapter<Contact> {
    public ProfileAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Contact c = getItem(position);

        //relationship_item only contains the TextView and not the CheckBox
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.relationship_item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.relContactName);
        name.setText(c.getName());

        return convertView;
    }
}

