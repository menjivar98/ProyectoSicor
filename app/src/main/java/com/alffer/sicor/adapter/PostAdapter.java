package com.alffer.sicor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.alffer.sicor.R;
import com.alffer.sicor.models.DangerPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<DangerPost> danger_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;

    public PostAdapter(List<DangerPost> danger_list){

        this.danger_list = danger_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item,parent,false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String title_data = danger_list.get(position).getTitle();
        holder.setTitlText(title_data);


        String desc_data = danger_list.get(position).getDescription();
        holder.setDescText(desc_data);

        String danger_post = danger_list.get(position).getDanger();
        holder.setPostDanger(danger_post);

        String image_url = danger_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        String user_id = danger_list.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName,userImage);

                }
            }
        });

        long milliseconds = danger_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
        holder.setTime(dateString);
    }

    @Override
    public int getItemCount() {
        return danger_list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView titlView;
        TextView desView;
        ImageView blogImageView;
        TextView dateView;
        TextView postUserName;
        CircleImageView postUserImage;
        TextView dangerView;
        ConstraintLayout cLayout;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setTitlText(String titlText) {
            titlView = mView.findViewById(R.id.post_title);
            titlView.setText(titlText);
        }

        private void  setDescText(String descText){
            desView = mView.findViewById(R.id.tv_description);
            desView.setText(descText);
        }

        private void setPostDanger(String danger) {
            dangerView = mView.findViewById(R.id.post_danger);
            cLayout = mView.findViewById(R.id.inBoxed);
            dangerView.setText(danger);
            if(danger.equals("Low")) {
                cLayout.setBackgroundColor(Color.parseColor("#ACC18A"));
            }
            if(danger.equals("Medium")) {
                cLayout.setBackgroundColor(Color.parseColor("#FBA16C"));
            }
            if(danger.equals("High")) {
                cLayout.setBackgroundColor(Color.parseColor("#D33E43"));
            }


        }

        private void setBlogImage(String downloadUri){

            blogImageView = mView.findViewById(R.id.post_image);
            Glide.with(context).load(downloadUri).into(blogImageView);

        }

        private void setTime(String time){

            dateView = mView.findViewById(R.id.post_date);
            dateView.setText(time);
        }

        private void setUserData(String name, String image){
            postUserName = mView.findViewById(R.id.post_username);
            postUserImage = mView.findViewById(R.id.post_user_image);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_profile);


            postUserName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(postUserImage);


        }

    }
}