package com.mobileapp.viral.contacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileapp.viral.contacts.adapters.ContactAdapter;
import com.mobileapp.viral.contacts.fragments.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, AddContactFragment.OnFragmentInteractionListener {
    private static final int REQ_CODE = 123;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private ContactAdapter adapter;
    private ListView contactList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSharedPrefs(); //read from sharedpreferences on initial load

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            contactList = (ListView) findViewById(R.id.contactsListView);
            adapter = new ContactAdapter(this, contacts);
            contactList.setAdapter(adapter);

            //event listener for when the user clicks on a specific contact in list
            contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    intent = new Intent(getApplicationContext(), ContactProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("selectedContact", contacts.get(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            //event listener for delete button
            Button delete = (Button) findViewById(R.id.deleteContactBtn);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Contact> deletedContacts = new ArrayList<Contact>();

                    //find all selected contacts and remove them from contacts list
                    for(int i = adapter.getCount() - 1; i >= 0; i--) {
                        if(adapter.getItem(i).isChecked()) {
                            deletedContacts.add(contacts.get(i));
                            contacts.remove(i);
                        }
                    }

                    //update adapter to reflect deletion of contacts
                    ((ArrayAdapter) contactList.getAdapter()).notifyDataSetChanged();

                    //also need to delete those contacts from relationships in existing contacts
                    for(int i = 0; i < adapter.getCount(); i++) {
                        for(int j = 0; j < deletedContacts.size(); j++) {
                            contacts.get(i).getRelationships().remove(deletedContacts.get(j));
                        }
                        contacts.get(i).setChecked(false);
                    }

                    //update adapter to reflect deletion of relationships
                    ((ArrayAdapter) contactList.getAdapter()).notifyDataSetChanged();
                    writeSharedPrefs(contacts);
                }
            });
        } else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.leftFrag, new ContactsFragment());
            fragmentTransaction.commit();
        }
    }

    //when user clicks add contact, start intent for contact details class and expect result
    public void addContactClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ContactDetails.class);
        startActivityForResult(intent, REQ_CODE);
    }

    //after user completes adding a contact, call addRelationships
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQ_CODE) {
            Contact newContact = intent.getParcelableExtra("newContact");
            addRelationships(newContact);
        }
    }

    //establish relationships between newly created contact and existing contacts that user selects
    public void addRelationships(Contact contact) {
        if(contact != null) {
            contacts.add(contact);
            ArrayList<Contact> relationshipList = contact.getRelationships();

            for (int i = 0; i < contacts.size(); i++) {
                for (int j = 0; j < relationshipList.size(); j++) {
                    if (relationshipList.get(j).getName().equals(contacts.get(i).getName())) {
                        contacts.get(i).getRelationships().add(contact);
                    }
                }
            }
            adapter.notifyDataSetChanged();
            writeSharedPrefs(contacts);
            contact = null;
        }
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

    //write contact list to SharedPrefrences using Gson to convert ArrayList to string
    public void writeSharedPrefs(ArrayList<Contact> contacts) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        String json = gson.toJson(contacts);
        editor.putString("contacts", json);
        editor.commit();
    }

    //display contact list fragment for landscape when add person is clicked
    @Override
    public void onAddPersonClicked(Contact contact)
    {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ContactsFragment contactsListFragment = ContactsFragment.newInstance(contact);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.leftFrag, contactsListFragment);
            fragmentTransaction.commit();
        }
    }

    //display new contact fragment for landscape when add contact is clicked
    @Override
    public void onAddContactClicked(ArrayList<Contact> contactsList)
    {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            AddContactFragment addContactFragment = AddContactFragment.newInstance(contactsList);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.rightFrag, addContactFragment);
            fragmentTransaction.commit();
        }
    }

    //display contact profile fragment for landscape when specific contact is clicked
    @Override
    public void onListItemClicked(Contact contact)
    {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ProfileFragment contactProfileFragment = ProfileFragment.newInstance(contact);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.rightFrag, contactProfileFragment);
            fragmentTransaction.commit();
        }
    }
}
