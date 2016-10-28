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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Adapter class for Relationship that handles establishing of a relationship
 * between contacts when a new contact is created.
 */
public class RelationshipAdapter extends ArrayAdapter<Contact> {
    private ArrayList<Contact> contacts;

    public RelationshipAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.contacts = contacts;
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
        CheckBox checkbox = null;

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

        if(contact != null) {
            TextView name = viewHolder.textView;
            name.setText(contact.getName());
            checkbox = viewHolder.checkBox;
        }

        checkbox.setOnCheckedChangeListener(null);
        if(contact.isChecked()) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if(isChecked) {
                contact.setChecked(true);
                contacts.get(position).setChecked(true);
                sortList();
            }
            else {
                contact.setChecked(false);
                contacts.get(position).setChecked(false);
                sortList();
            }
            }
        });

        return convertView;
    }

    //move selected contacts to the top of the list by sorting list based on checkBoxState
    public void sortList() {
        ArrayList<Contact> selectedContacts = new ArrayList<Contact>();
        selectedContacts.addAll(contacts);
        Collections.sort(selectedContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
            if((c1.isChecked() && c2.isChecked()) || (!(c1.isChecked()) && !(c2.isChecked()))) {
                return 0;
            }
            else if(c1.isChecked() && !(c2.isChecked())) {
                return -1;
            }
            else {
                return 1;
            }
            }
        });

        contacts.clear();
        contacts.addAll(selectedContacts);
        notifyDataSetChanged();
    }
}
