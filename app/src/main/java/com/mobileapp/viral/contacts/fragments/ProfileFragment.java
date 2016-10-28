package com.mobileapp.viral.contacts.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileapp.viral.contacts.Contact;
import com.mobileapp.viral.contacts.adapters.ProfileAdapter;
import com.mobileapp.viral.contacts.R;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ArrayList<Contact> relationships = new ArrayList<Contact>();
    private ProfileAdapter adapter;
    private Contact selectedContact;

    public interface OnFragmentInteractionListener {
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

    public ProfileFragment() { }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public static ProfileFragment newInstance(Contact selectedContact) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedContact", selectedContact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            selectedContact = getArguments().getParcelable("selectedContact");
            relationships = selectedContact.getRelationships();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView contactName = (TextView) view.findViewById(R.id.contactNameInput);
        TextView contactPhone = (TextView) view.findViewById(R.id.contactPhoneInput);
        contactName.setText(selectedContact.getName());
        contactPhone.setText(selectedContact.getPhone());
        contactName.setFocusable(false);
        contactPhone.setFocusable(false);

        adapter = new ProfileAdapter(getActivity(), relationships);
        ListView relationshipListVIew = (ListView) view.findViewById(R.id.relationshipListView);
        relationshipListVIew.setAdapter(adapter);

        relationshipListVIew.setOnItemClickListener(null);
        relationshipListVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onListItemClicked(selectedContact.getRelationships().get(position));
            }
        });
    }
}
