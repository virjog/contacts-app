package com.mobileapp.viral.contacts.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileapp.viral.contacts.Contact;
import com.mobileapp.viral.contacts.R;
import com.mobileapp.viral.contacts.adapters.RelationshipAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddContactFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private RelationshipAdapter adapter;
    private ListView contactList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson = new Gson();

    public interface OnFragmentInteractionListener {
        public void onAddPersonClicked(Contact contact);
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

    public AddContactFragment() { }

    public static AddContactFragment newInstance() {
        AddContactFragment fragment = new AddContactFragment();
        return fragment;
    }

    public static AddContactFragment newInstance(ArrayList<Contact> contacts)
    {
        AddContactFragment addContactFragment = new AddContactFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("contacts", contacts);
        addContactFragment.setArguments(args);
        return addContactFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            contacts = getArguments().getParcelable("contacts");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        readSharedPrefs();

        final EditText name = (EditText) getActivity().findViewById(R.id.contactNameInput);
        final EditText phone = (EditText) getActivity().findViewById(R.id.contactPhoneInput);
        adapter = new RelationshipAdapter(getActivity(), contacts);
        contactList = (ListView) view.findViewById(R.id.relationshipListViewFrag);
        contactList.setAdapter(adapter);

        Button add = (Button) view.findViewById(R.id.addPersonBtnFrag);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Contact newContact = new Contact(name.getText().toString(), phone.getText().toString());

            for(int i = adapter.getCount() - 1; i >= 0; i--) {
                if(adapter.getItem(i).isChecked()) {
                    newContact.getRelationships().add(contacts.get(i));
                }
            }
            mListener.onAddPersonClicked(newContact);
            writeSharedPrefs(contacts);
            }
        });
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
}
