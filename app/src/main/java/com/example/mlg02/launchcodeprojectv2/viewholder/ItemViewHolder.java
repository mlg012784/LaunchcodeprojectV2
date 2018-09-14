package com.example.mlg02.launchcodeprojectv2.viewholder;


import android.support.v7.widget.RecyclerView;

import android.view.View;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.mlg02.launchcodeprojectv2.R;
import com.example.mlg02.launchcodeprojectv2.model.Item;
public class ItemViewHolder extends RecyclerView.ViewHolder{

    public TextView itemText;
    public ImageView delView;
    public CheckBox checkBox;




    public ItemViewHolder(View itemView){
        super(itemView);


        itemText = itemView.findViewById(R.id.item_text);
        delView = itemView.findViewById(R.id.item_delete);
        checkBox = itemView.findViewById(R.id.check_Box);

        }


    public void bindToItem(Item item, CompoundButton.OnCheckedChangeListener checkedChangeListener){

        checkBox.setOnCheckedChangeListener(checkedChangeListener);

        itemText.setText(item.title);







    }


}





