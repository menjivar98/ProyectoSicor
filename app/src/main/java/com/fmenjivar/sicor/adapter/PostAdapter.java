package com.fmenjivar.sicor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fmenjivar.sicor.R;
import com.fmenjivar.sicor.models.DangerPost;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<DangerPost> danger_list;


    public PostAdapter(List<DangerPost> danger_list){

        this.danger_list = danger_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String desc_data = danger_list.get(position).getDescription();
        holder.setDescText(desc_data);

    }

    @Override
    public int getItemCount() {
        return danger_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView desView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void  setDescText(String descText){
            desView = mView.findViewById(R.id.post_desc);
            desView.setText(descText);
        }

    }
}
