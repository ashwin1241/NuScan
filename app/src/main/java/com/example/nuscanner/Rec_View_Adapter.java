package com.example.nuscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Rec_View_Adapter extends RecyclerView.Adapter<Rec_View_Adapter.Rec_View_Holder> {

    private ArrayList<Card_item> arrayList;
    private int selecttype = 0;

    public static class Rec_View_Holder extends RecyclerView.ViewHolder
    {
        private ImageView card_image;
        private ImageView card_share;
        private ImageView card_select;
        private ImageView card_unselect;
        private TextView card_title;
        private TextView card_date;

        public Rec_View_Holder(@NonNull View itemView) {
            super(itemView);

            card_image = itemView.findViewById(R.id.card_image);
            card_select = itemView.findViewById(R.id.card_selected);
            card_unselect = itemView.findViewById(R.id.card_unselected);
            card_share = itemView.findViewById(R.id.card_share);
            card_title = itemView.findViewById(R.id.card_title);
            card_date = itemView.findViewById(R.id.card_date);

        }
    }

    public Rec_View_Adapter(ArrayList<Card_item> list)
    {
        this.arrayList = list;
    }

    @Override
    public Rec_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
        Rec_View_Holder rvh = new Rec_View_Holder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(Rec_View_Adapter.Rec_View_Holder holder, int position) {
        Card_item current_item = arrayList.get(position);
        holder.card_title.setText(current_item.getTitle());
        holder.card_date.setText(current_item.getDate());
        if(current_item.isSelected()==true)
        {
            holder.card_share.setVisibility(View.INVISIBLE);
            holder.card_unselect.setVisibility(View.INVISIBLE);
            holder.card_select.setVisibility(View.VISIBLE);
        }
        else if(current_item.isSelected()==false&&this.selecttype==1)
        {
            holder.card_share.setVisibility(View.INVISIBLE);
            holder.card_unselect.setVisibility(View.VISIBLE);
            holder.card_select.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.card_share.setVisibility(View.VISIBLE);
            holder.card_unselect.setVisibility(View.INVISIBLE);
            holder.card_select.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setSelecttype(int selecttype) {
        this.selecttype = selecttype;
    }
}
