package com.example.nuscan;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rec_View_Sub_Adatper extends RecyclerView.Adapter<Rec_View_Sub_Adatper.Rec_View_Sub_Holder>{

    private List<Card_sub_item> list;
    private OnItemClickListener mListener;
    private Context context124;

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public interface OnItemClickListener
    {
        void OnItemClicked(int position);
        void OnTitleClicked(int position);
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

    public Rec_View_Sub_Adatper(ArrayList<Card_sub_item> arrayList, Context context)
    {
        this.list = arrayList;
        this.context124 = context;
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
        File file = new File(context124.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + current_sub_item.getName());
        if(file.exists())
        holder.card_sub_image.setImageURI(Uri.parse(current_sub_item.getImage()));
        else
        holder.card_sub_image.setImageResource(R.drawable.ic_outline_image_not_supported_24);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
