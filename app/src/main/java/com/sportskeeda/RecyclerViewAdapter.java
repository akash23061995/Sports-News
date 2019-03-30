package com.sportskeeda;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context context; String  newslink;
    List<News> mData;String link;
    public Activity activity;
     private ListItemClickListener mOnClickListener;
    public interface ListItemClickListener{
        void onListItemClick(int itemIndex, String link);
    }
    public RecyclerViewAdapter(Context context, List<News> mData, Activity activity, ListItemClickListener listener) {
        this.context = context;
        this.mData = mData;
        this.activity=activity;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v=LayoutInflater.from(context).inflate(R.layout.row,viewGroup,false);
        MyViewHolder myViewHolder= new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.title.setText(mData.get(position).getTitle());
        holder.author.setText(mData.get(position).getAuthor());
        holder.card.setTag(position);
        String img = mData.get(position).getImg();

        if(img.length()<2){
            holder.img.setImageResource(R.drawable.notavailable);
        }

        else
        Picasso.with(context).load(mData.get(position).getImg()).into(holder.img);
//
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public   class MyViewHolder extends  RecyclerView.ViewHolder   implements View.OnLongClickListener {

        private TextView title,author;
        private ImageView img;
        CardView card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.titlenews);
            author=itemView.findViewById(R.id.author);
            img= itemView.findViewById(R.id.img);
            card= itemView.findViewById(R.id.card);

        itemView.setOnLongClickListener(this);

        }







        @Override
        public boolean onLongClick(View v) {

            final int position= getAdapterPosition();

            mOnClickListener.onListItemClick(position,newslink);
            return true;
        }
    }
    public  void updateList(List<News>  newList){
        mData= new ArrayList<>();
        mData.addAll(newList);
        notifyDataSetChanged();
    }


}
