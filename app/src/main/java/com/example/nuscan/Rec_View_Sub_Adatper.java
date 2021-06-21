package com.example.nuscan;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Rec_View_Sub_Adatper extends RecyclerView.Adapter<Rec_View_Sub_Adatper.Rec_View_Sub_Holder>{

    private List<Card_sub_item> list;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public interface OnItemClickListener
    {
        void OnItemClicked(int position);
        void OnTitleClicked(int position);
        void OnItemLongClicked(int position);
    }

    public static class Rec_View_Sub_Holder extends RecyclerView.ViewHolder
    {
        private ImageView card_sub_image;
        private TextView card_sub_title;

        public Rec_View_Sub_Holder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            card_sub_image = itemView.findViewById(R.id.sub_image);
            card_sub_title = itemView.findViewById(R.id.sub_title);

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
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION)
                    {
                        listener.OnItemLongClicked(position);
                    }
                    return false;
                }
            });

            card_sub_title.setOnClickListener(new View.OnClickListener() {
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

    public Rec_View_Sub_Adatper(ArrayList<Card_sub_item> arrayList)
    {
        this.list = arrayList;
    }

    @NonNull
    @Override
    public Rec_View_Sub_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sub_item,parent,false);
        Rec_View_Sub_Adatper.Rec_View_Sub_Holder rvh = new Rec_View_Sub_Adatper.Rec_View_Sub_Holder(v, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull Rec_View_Sub_Holder holder, int position) {
        Card_sub_item current_sub_item = list.get(position);
        holder.card_sub_title.setText(current_sub_item.getTitle());
        if(current_sub_item.getImage()!=null)
        {
            Picasso.get().load(Uri.parse(current_sub_item.getImage())).placeholder(R.drawable.ic_loader_svg).into(holder.card_sub_image);
        }
        else
        {
            holder.card_sub_image.setImageResource(R.drawable.ic_loader_svg);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
