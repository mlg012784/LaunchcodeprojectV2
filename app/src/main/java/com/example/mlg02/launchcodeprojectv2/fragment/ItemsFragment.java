package com.example.mlg02.launchcodeprojectv2.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ItemsFragment extends ItemListFragment{

    public ItemsFragment(){}

    @Override
    public Query getQuery(DatabaseReference databaseReference){


        Query itemsQuery = databaseReference.child("items").orderByKey();

        return itemsQuery;
    }
}
