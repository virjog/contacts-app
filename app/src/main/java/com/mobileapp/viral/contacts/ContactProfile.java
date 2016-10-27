package com.mobileapp.viral.contacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileapp.viral.contacts.adapters.ProfileAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Class for Contact Profile activity
 */
public class ContactProfile extends AppCompatActivity {
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> relationshipList;
    private ProfileAdapter adapter;
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile);
        setTitle("Contact Profile");
        readSharedPrefs();

        /**
         * retrieve contact object that user taps in the contacts list (or from another
         * contact's relationship)
         */
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        Contact selectedContact = bundle.getParcelable("selectedContact");

        //populate name and phone fields with selected contact's info
        TextView contactName = (TextView) findViewById(R.id.contactNameInput);
        TextView contactPhone = (TextView) findViewById(R.id.contactPhoneInput);
        contactName.setText(selectedContact.getName());
        contactPhone.setText(selectedContact.getPhone());
        contactName.setFocusable(false);
        contactPhone.setFocusable(false);

        //set up relationship listview
        relationshipList = selectedContact.getRelationships();
        adapter = new ProfileAdapter(this, relationshipList);
        ListView relationships = (ListView) findViewById(R.id.relationshipListView);
        relationships.setAdapter(adapter);

        //if user taps a name in the relationships of a contact, take them to their profile
        relationships.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact parentContact = relationshipList.get(position);
                Contact selectedContact = null;

                for(int i = 0; i < contacts.size(); i++) {
                    if(contacts.get(i).getName().equals(parentContact.getName())) {
                        selectedContact = contacts.get(i);
                    }
                }

                //store that contact as Parcelable object in intent
                intent = new Intent(getApplicationContext(), ContactProfile.class);
                intent.putExtra("selectedContact", (Parcelable) selectedContact);
                startActivity(intent);
            }
        });
    }

    //read contacts from SharedPrefrences using Gson to convert string to ArrayList
    public void readSharedPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String json = prefs.getString("contacts", null);
        if(json != null) {
            Type arrayListObject = new TypeToken<ArrayList<Contact>>(){}.getType();
            contacts = gson.fromJson(json, arrayListObject);
        }
        else {
            contacts = new ArrayList<Contact>();
        }
    }
}
