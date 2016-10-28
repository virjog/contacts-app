package com.mobileapp.viral.contacts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobileapp.viral.contacts.Contact;
import com.mobileapp.viral.contacts.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Adapter class for displaying a contact item in a ListView and setting the checkboxes
 * of a selected contact accordingly.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    //ViewHolder class to make it easier to access specific contact TextView objects
    public static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Contact contact = getItem(position);
        ViewHolder viewHolder = new ViewHolder();

        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.contact_item, parent, false);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.contactNameTextView);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView name = viewHolder.textView;
        name.setText(contact.getName());
        CheckBox checkBox = viewHolder.checkBox;

        if(contact.isChecked()) {
            checkBox.setChecked(true);
        }
        else {
            checkBox.setChecked(false);
        }

        /**
         * Event handler for setting a checkbox as selected or not based on
         * when a user taps on it
         */
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if(isChecked) {
                getItem(position).setChecked(true);
            }
            else {
                getItem(position).setChecked(false);
            }
            }
        });

        return convertView;
    }
}
