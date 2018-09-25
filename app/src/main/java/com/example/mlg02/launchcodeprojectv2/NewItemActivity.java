package com.example.mlg02.launchcodeprojectv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlg02.launchcodeprojectv2.model.Item;
import com.example.mlg02.launchcodeprojectv2.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;



public class NewItemActivity extends BaseActivity {

    private static final String TAG = "NewItemActivity";
    private static final String REQUIRED ="Required";
    public static final String EXTRA_ITEM_KEY = "item_key";

    //database ref
    private String mItemKey;
    private DatabaseReference mDatabase;
    private ValueEventListener mItemListener;

    private EditText mTitleField;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);


        //get item key
        mItemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);

        mTitleField = findViewById(R.id.field_title);
        mBodyField = findViewById(R.id.field_body);
        mSubmitButton = findViewById(R.id.fab_submit_post);
        //Initialize Database
        mDatabase = FirebaseDatabase.getInstance().getReference();



    }

   @Override
   public void onStart(){
       super.onStart();

       if (mItemKey != null) {
           ValueEventListener itemListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   Item item = dataSnapshot.getValue(Item.class);

                   if (item != null) {
                       mTitleField.setText(item.title, TextView.BufferType.EDITABLE);
                       mBodyField.setText(item.body, TextView.BufferType.EDITABLE);
                       mTitleField.setSelectAllOnFocus(true);
                       mBodyField.setSelectAllOnFocus(true);
                   }


               }


               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
                   Log.w(TAG, "getItem:onCancelled: ", databaseError.toException());
                   setEditingEnabled(true);
               }
           };
           mDatabase.child("items").child(mItemKey).addValueEventListener(itemListener);

           //copy of listener to be removed when activity stops
           mItemListener = itemListener;

           mSubmitButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   editItem();
               }
           });

       } else{
           mSubmitButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   submitItem();
               }
           });
       }
   }





    @Override public void onStop(){
        super.onStop();

        //remove item value event listener
        if (mItemListener != null){
           mDatabase.removeEventListener(mItemListener);
        }
    }

    private void editItem(){

        final String userId = getUid();
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        //Title validation
        if(TextUtils.isEmpty(title)){
            mTitleField.setError(REQUIRED);
            return;
        }
        //Body validation
        if (TextUtils.isEmpty(body)){
            mBodyField.setError(REQUIRED);
            return;
        }
        //Disable Button
        setEditingEnabled(false);
        Toast.makeText(this, "Editing Item...", Toast.LENGTH_SHORT).show();
        updateItem(userId,title,body);

        //finish activity back to prev
        setEditingEnabled(true);
        finish();
    }

    private void submitItem(){
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        //Title validation
        if(TextUtils.isEmpty(title)){
            mTitleField.setError(REQUIRED);
            return;
        }
        //Body validation
        if (TextUtils.isEmpty(body)){
            mBodyField.setError(REQUIRED);
            return;
        }
        //Disable Button
        setEditingEnabled(false);
        Toast.makeText(this, "Adding Item...", Toast.LENGTH_SHORT).show();

        //read from DB single
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        //get user
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null){
                            //user null error
                            Log.e(TAG,"User " + userId + " null");
                            Toast.makeText(NewItemActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            //Write item to Db
                            writeNewItem(userId, user.username,title, body);
                        }
                        //finish activity back to prev
                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled: ", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }

    }

    private void updateItem (String userId,String title, String body){

        String key = mItemKey;

        Map<String,Object> itemValues = new HashMap<>();
        itemValues.put("uid",userId);
        itemValues.put("title",title);
        itemValues.put("body",body);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/items/" + key, itemValues);
        childUpdates.put("/user-items/" + userId + "/" + key, itemValues);
        mDatabase.updateChildren(childUpdates);

    }

    private void writeNewItem(String userId, String username, String title, String body) {

        String key = mDatabase.child("items").push().getKey();
        Item item = new Item(userId, username, title, body);
        Map<String, Object> itemValues = item.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/items/" + key, itemValues);
        childUpdates.put("/user-items/" + userId + "/" + key, itemValues);

        mDatabase.updateChildren(childUpdates);

    }

}
