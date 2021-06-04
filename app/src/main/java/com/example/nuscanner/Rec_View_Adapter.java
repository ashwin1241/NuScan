package com.example.nuscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Rec_View_Adapter extends RecyclerView.Adapter<Rec_View_Adapter.Rec_View_Holder>{

    private List<Card_item> arrayList;
    private int selecttype = 0;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public interface OnItemClickListener
    {
        void OnItemClicked(int position);
        void OnItemLongClicked(int position);
        void OnItemShared(int position);
        void OnTitleClicked(int position);
    }

    public static class Rec_View_Holder extends RecyclerView.ViewHolder
    {
        public ImageView card_image;
        public ImageView card_share;
        public ImageView card_select;
        public ImageView card_unselect;
        public TextView card_title;
        public TextView card_date;

        public Rec_View_Holder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);

            card_image = itemView.findViewById(R.id.card_image);
            card_select = itemView.findViewById(R.id.card_selected);
            card_unselect = itemView.findViewById(R.id.card_unselected);
            card_share = itemView.findViewById(R.id.card_share);
            card_title = itemView.findViewById(R.id.card_title);
            card_date = itemView.findViewById(R.id.card_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.OnItemClicked(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.OnItemLongClicked(position);
                        }
                    }
                    return false;
                }
            });

            card_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.OnItemShared(position);
                        }
                    }
                }
            });

            card_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.OnTitleClicked(position);
                        }
                    }
                }
            });

        }
    }

    public Rec_View_Adapter(ArrayList<Card_item> list) { this.arrayList = list;}

    @Override
    public Rec_View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
        Rec_View_Holder rvh = new Rec_View_Holder(v,mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull Rec_View_Adapter.Rec_View_Holder holder, int position) {
        Card_item current_item = arrayList.get(position);
        holder.card_title.setText(current_item.getTitle());
        holder.card_date.setText(current_item.getDate());
        holder.card_image.setImageResource(R.drawable.ic_sharp_insert_drive_file_90);
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
        return arrayList.size();
    }

    public void setSelecttype(int selecttype) {
        this.selecttype = selecttype;
    }
    public int getSelecttype() { return this.selecttype; }

    public void filterList(ArrayList<Card_item> filterList)
    {
        arrayList = filterList;
        notifyDataSetChanged();
    }

}
