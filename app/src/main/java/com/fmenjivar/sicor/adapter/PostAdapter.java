package com.fmenjivar.sicor.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fmenjivar.sicor.R;
import com.fmenjivar.sicor.models.DangerPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<DangerPost> danger_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public PostAdapter(List<DangerPost> danger_list){

        this.danger_list = danger_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item,parent,false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String dangerPostId = danger_list.get(position).DangerPostID;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = danger_list.get(position).getDescription();
        holder.setDescText(desc_data);

        String image_url = danger_list.get(position).getImage_url();
        String thumbUri = danger_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url,thumbUri);

        String user_id = danger_list.get(position).getUser_id();

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName,userImage);


                }else{

                }
            }
        });

        long milliseconds = danger_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();

        //Likes Feature
        holder.DangerLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> likeMap = new HashMap<>();
                likeMap.put("timestamp", FieldValue.serverTimestamp());



                firebaseFirestore.collection("Post/" + dangerPostId + "/Likes").document(currentUserId)
                        .set(likeMap);

            }
        });

        holder.setTime(dateString);
    }

    @Override
    public int getItemCount() {
        return danger_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView desView;
        ImageView blogImageView;
        TextView dateView;
        TextView postUserName;
        CircleImageView postUserImage;
        ImageView DangerLikeBtn;
        TextView DangerLikeCount;


        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            DangerLikeBtn = mView.findViewById(R.id.post_like_btn);

        }

        private void  setDescText(String descText){
            desView = mView.findViewById(R.id.post_desc);
            desView.setText(descText);
        }

        private void setBlogImage(String downloadUri,String thumbUri){

            blogImageView = mView.findViewById(R.id.post_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholderimage);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

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
