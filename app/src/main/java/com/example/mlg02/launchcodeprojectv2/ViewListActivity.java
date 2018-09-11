package com.example.mlg02.launchcodeprojectv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.example.mlg02.launchcodeprojectv2.fragment.ItemListFragment;
import com.example.mlg02.launchcodeprojectv2.fragment.ItemsFragment;
import com.example.mlg02.launchcodeprojectv2.model.Item;
import com.example.mlg02.launchcodeprojectv2.viewholder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ViewListActivity extends BaseActivity{

    private static final String TAG = "ViewListActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new ItemsFragment()
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;

            }
        };

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);


        findViewById(R.id.add_item_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(ViewListActivity.this, NewItemActivity.class));
            }

        });
    }




}
