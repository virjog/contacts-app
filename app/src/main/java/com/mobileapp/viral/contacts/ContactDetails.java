package com.mobileapp.viral.contacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileapp.viral.contacts.adapters.RelationshipAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Class for Contact Details activity
 */
public class ContactDetails extends AppCompatActivity {
    private ListView relationshipListView;
    private ArrayList<Contact> contacts;
    private RelationshipAdapter adapter;
    private SharedPreferences prefs;
    private Gson gson = new Gson();

    /**
     * Read from SharedPreferences the contacts list
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_details);
        setTitle("Contact Details");
        readSharedPrefs();

        //set up RelationshipAdapter
        relationshipListView = (ListView) findViewById(R.id.relationshipListView);
        adapter = new RelationshipAdapter(this, contacts);
        relationshipListView.setAdapter(adapter);
    }

    /**
     * When user presses "Add Person" button, create a new contact and populate
     * the name and phone fields and add any selected contacts to the new contact's
     * relationships ArrayList. Store the new contact as Parcelabel object in an intent.
     * @param view
     */
    public void onAddPerson(View view) {
        EditText name = (EditText) findViewById(R.id.contactNameInput);
        EditText phone = (EditText) findViewById(R.id.contactPhoneInput);
        Contact newContact = new Contact(name.getText().toString(), phone.getText().toString());

        for(int i = adapter.getCount() - 1; i >= 0; i--) {
            if(adapter.getItem(i).isChecked()) {
                newContact.getRelationships().add(contacts.get(i));
            }
        }

        Intent intent = new Intent();
        intent.putExtra("newContact", (Parcelable) newContact);
        setResult(RESULT_OK, intent);
        finish();
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
