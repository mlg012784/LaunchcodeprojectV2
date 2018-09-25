package com.example.mlg02.launchcodeprojectv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlg02.launchcodeprojectv2.model.Item;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ItemDetailActivity";

    public static final String EXTRA_ITEM_KEY = "item_key";

    private DatabaseReference mItemReference;
    private ValueEventListener mItemListener;
    private String mItemKey;
    private boolean del;
    private boolean isChecked;

    private TextView mTitleView;
    private TextView mBodyView;
    private FloatingActionButton mNewItemButton;
    private FloatingActionButton mEditItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        //get del bool values
        isChecked = getIntent().getBooleanExtra("checkBoxValue", false);
        del = getIntent().getBooleanExtra("DELETE",false);
        //get item key
        mItemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (mItemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_ITEM_KEY");
        }

        //Initialize Database
        mItemReference = FirebaseDatabase.getInstance().getReference();

        //Initialize Views
        mTitleView = findViewById(R.id.item_title);
        mBodyView = findViewById(R.id.item_body);

        mNewItemButton = findViewById(R.id.fab_add_item);
        mEditItemButton = findViewById(R.id.fab_edit);
        mNewItemButton.setOnClickListener(this);
        mEditItemButton.setOnClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();

        if (del) {
            mNewItemButton.setVisibility(View.GONE);
            mEditItemButton.setVisibility(View.GONE);
            dialogBox();
        }

            //Add value event listener to item
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Item object and update UI
                Item item = dataSnapshot.getValue(Item.class);

                if (item != null && !isChecked ) {
                    mTitleView.setText(item.title);
                    mBodyView.setText(item.body);
                }else if (item != null){
                    mTitleView.setText(item.title);
                    mBodyView.setText("Objective completed");
                } else{
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ",databaseError.toException());

                Toast.makeText(ItemDetailActivity.this,"Load post, failed.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mItemReference.child("items").child(mItemKey).addValueEventListener(itemListener);

        //copy of listener to be removed when activity stops
        mItemListener = itemListener;
    }

    @Override public void onStop(){
        super.onStop();

        //remove item value event listener
        if (mItemListener != null){
            mItemReference.removeEventListener(mItemListener);
        }
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.fab_edit){
            editItem();
        } else if(i == R.id.fab_add_item) {
            Intent intent = new Intent(this, NewItemActivity.class);
            startActivity(intent);
        }
    }

    private void editItem(){
        //Launch NewItemActivity passing "item_key"
        Intent intent = new Intent(this, NewItemActivity.class);
        intent.putExtra("item_key", mItemKey);
        startActivity(intent);

    }

    private void deleteItem(){
        final String userId = getUid();
        String key = mItemKey;

        mItemReference.child("items").child(key).removeValue();
        mItemReference.child("user-items").child(userId).child(key).removeValue();
        Toast.makeText(ItemDetailActivity.this,"Item deleted.",
                Toast.LENGTH_SHORT).show();
    }

    private void dialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Delete Item?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteItem();
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog delAlert = builder.create();
        delAlert.show();
    }
}
