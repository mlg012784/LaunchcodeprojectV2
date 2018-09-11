package com.example.mlg02.launchcodeprojectv2.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.mlg02.launchcodeprojectv2.ItemDetailActivity;
import com.example.mlg02.launchcodeprojectv2.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.example.mlg02.launchcodeprojectv2.model.Item;
import com.example.mlg02.launchcodeprojectv2.viewholder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public abstract class ItemListFragment extends Fragment {

    private static final String TAG = "ItemListFragment";

    //data base reference
    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Item, ItemViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    public ItemListFragment() {}

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_items,container,false);

        //database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.item_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //layout  manager
        mManager = new LinearLayoutManager(getActivity());

        mRecycler.setLayoutManager(mManager);

        //Firebase
        Query itemQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(itemQuery, Item.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {


            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ItemViewHolder(inflater.inflate(R.layout.list_item, viewGroup,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final ItemViewHolder viewHolder, int position, @NonNull final Item model) {
                final DatabaseReference itemRef = getRef(position);

                //set onClickListener for card

                final String uId = getUid();
                final String itemKey = itemRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Launch ItemDetailActivity

                        Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                        intent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, itemKey);
                        intent.putExtra("checkBoxValue", viewHolder.checkBox.isChecked());

                        startActivity(intent);
                    }
                });
                //set onClickListener for delete
                viewHolder.delView.setOnClickListener(new View.OnClickListener() {
                    final boolean del = true;
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                        intent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, itemKey);
                        intent.putExtra("DELETE", del);
                        startActivity(intent);

                    }
                });

                viewHolder.bindToItem(model, new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            viewHolder.itemText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                        } else {
                            viewHolder.itemText.setPaintFlags(0);
                        }
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);


}
