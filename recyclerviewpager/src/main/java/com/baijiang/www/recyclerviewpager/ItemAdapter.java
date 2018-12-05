package com.baijiang.www.recyclerviewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Micky on 2018/12/3.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{

    private Context context;
    private List<ItemBean> mList;

    public ItemAdapter(Context context, List<ItemBean> list)
    {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.simpleDraweeView.setImageURI(mList.get(position).getCover());
        holder.textView.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView simpleDraweeView;
        TextView textView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.item_cover);
            textView = itemView.findViewById(R.id.item_name);
        }
    }
}
