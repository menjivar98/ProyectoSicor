package com.alffer.sicor.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alffer.sicor.R;
import com.alffer.sicor.adapter.PostAdapter;
import com.alffer.sicor.models.DangerPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

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
    private DocumentSnapshot lastVisible;
    private Boolean isFirstpageFirstLoad = true;
    private FloatingActionButton addPost;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addPost = getActivity().findViewById(R.id.add_post_btn);
        addPost.show();

        danger_list = new ArrayList<>();
        post_list_view = view.findViewById(R.id.post_list_view);

        postAdapter = new PostAdapter(danger_list);

        post_list_view.setAdapter(postAdapter);
        post_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

       firebaseAuth = FirebaseAuth.getInstance();

       if (firebaseAuth.getCurrentUser() != null){
           firebaseFirestore = FirebaseFirestore.getInstance();

          /* post_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
               @Override
               public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                   super.onScrollStateChanged(recyclerView, newState);

                    Boolean reachedBottom =  !recyclerView.canScrollVertically(-1);

                    if (reachedBottom){
                        /*
                        String desc = lastVisible.getString("description");

                        Toast.makeText(container.getContext(),"Reached: " + desc,Toast.LENGTH_SHORT ).show();
                        */

              /*          loadMorePost();

                    }
               }
           });*/

           Query firstQuery = firebaseFirestore.collection("Post").orderBy("timestamp",Query.Direction.DESCENDING);

           firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                   if (e!=null){
                       Log.d(TAG,"Error: " + e.getMessage());
                   }else{

                       if (isFirstpageFirstLoad){
                           lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);
                       }

                       for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                           if(doc.getType() == DocumentChange.Type.ADDED){

                               DangerPost dangerPost = doc.getDocument().toObject(DangerPost.class);

                               if (isFirstpageFirstLoad){
                                   danger_list.add(dangerPost);
                               }else {
                                   danger_list.add(0,dangerPost);
                               }

                               postAdapter.notifyDataSetChanged();


                           }
                       }

                       isFirstpageFirstLoad = false;
                   }
               }
           });

       }

       return view;
    }

  /*  public void loadMorePost(){

        Query nextQuery = firebaseFirestore.collection("Post")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() -1);
                    for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                        if(doc.getType() == DocumentChange.Type.ADDED){

                            DangerPost dangerPost = doc.getDocument().toObject(DangerPost.class);
                            danger_list.add(dangerPost);

                            postAdapter.notifyDataSetChanged();


                        }
                    }
                }else {
                    Log.d(TAG,"Error: " + e.getMessage());
                }

            }
        });

    }*/

}
