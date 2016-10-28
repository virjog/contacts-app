package com.mobileapp.viral.contacts.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileapp.viral.contacts.Contact;
import com.mobileapp.viral.contacts.adapters.ContactAdapter;
import com.mobileapp.viral.contacts.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ContactsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private ContactAdapter adapter;
    private Contact contact;
    private ListView contactList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();

    public interface OnFragmentInteractionListener {
        public void onAddContactClicked(ArrayList<Contact> contacts);
        public void onListItemClicked(Contact contact);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ContactsFragment() { }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    public static ContactsFragment newInstance(Contact contact) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putParcelable("contact", contact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            contact = getArguments().getParcelable("contact");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readSharedPrefs();

        adapter = new ContactAdapter(getActivity(), contacts);
        contactList = (ListView) view.findViewById(R.id.contactsListViewFrag);
        contactList.setAdapter(adapter);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onListItemClicked(contacts.get(position));
            }
        });

        Button add = (Button) view.findViewById(R.id.addContactBtnFrag);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddContactClicked(contacts);
            }
        });

        Button delete = (Button) view.findViewById(R.id.deleteContactBtnFrag);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Contact> deletedContacts = new ArrayList<Contact>();

                for(int i = adapter.getCount() - 1; i >= 0; i--) {
                    if(adapter.getItem(i).isChecked()) {
                        deletedContacts.add(contacts.get(i));
                        contacts.remove(i);
                    }
                }

                adapter.notifyDataSetChanged();

                for(int i = 0; i < adapter.getCount(); i++) {
                    for(int j = 0; j < deletedContacts.size(); j++) {
                        contacts.get(i).getRelationships().remove(deletedContacts.get(j));
                    }
                    contacts.get(i).setChecked(false);
                }

                adapter.notifyDataSetChanged();
                writeSharedPrefs(contacts);
            }
        });

        addRelationships(contact);
    }

    public void readSharedPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String json = prefs.getString("contacts", null);
        if(json != null)
        {
            Type arrayListObject = new TypeToken<ArrayList<Contact>>(){}.getType();
            contacts = gson.fromJson(json, arrayListObject);
        }
        else
        {
            contacts = new ArrayList<Contact>();
        }
    }

    public void writeSharedPrefs(ArrayList<Contact> contacts) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();
        String json = gson.toJson(contacts);
        editor.putString("contacts", json);
        editor.commit();
    }

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
}
