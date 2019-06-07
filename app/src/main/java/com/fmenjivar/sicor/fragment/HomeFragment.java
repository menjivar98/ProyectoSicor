package com.fmenjivar.sicor.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmenjivar.sicor.R;
import com.fmenjivar.sicor.adapter.PostAdapter;
import com.fmenjivar.sicor.models.DangerPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView post_list_view;
    private List<DangerPost> danger_list;
    PostAdapter postAdapter;

    //Firebase
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        danger_list = new ArrayList<>();
        post_list_view = view.findViewById(R.id.post_list_view);

        postAdapter = new PostAdapter(danger_list);

        post_list_view.setAdapter(postAdapter);
        post_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

       firebaseAuth = FirebaseAuth.getInstance();


       if (firebaseAuth.getCurrentUser() != null){
           firebaseFirestore = FirebaseFirestore.getInstance();

           Query firstQuery = firebaseFirestore.collection("Post").orderBy("timestamp",Query.Direction.DESCENDING);

          firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                   for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                       if(doc.getType() == DocumentChange.Type.ADDED){

                           DangerPost dangerPost = doc.getDocument().toObject(DangerPost.class);
                           danger_list.add(dangerPost);

                           postAdapter.notifyDataSetChanged();


                       }
                   }
               }
           });


       }

       return view;
    }

}
