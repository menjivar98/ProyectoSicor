package com.fmenjivar.sicor.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmenjivar.sicor.R;
import com.fmenjivar.sicor.adapter.PostAdapter;
import com.fmenjivar.sicor.models.DangerPost;
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
public class MyPosts extends Fragment {

    private List<DangerPost> danger_list;
    private PostAdapter postAdapter;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstpageFirstLoad = true;

    public MyPosts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton addPost = getActivity().findViewById(R.id.add_post_btn);
        addPost.show();

        danger_list = new ArrayList<>();
        RecyclerView post_list_view = view.findViewById(R.id.post_list_view);

        postAdapter = new PostAdapter(danger_list);

        post_list_view.setAdapter(postAdapter);
        post_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            //Firebase
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseAuth = FirebaseAuth.getInstance();

            String user_id = firebaseAuth.getCurrentUser().getUid();

            Query firstQuery = firebaseFirestore.collection("Post").whereEqualTo("user_id", user_id).orderBy("timestamp", Query.Direction.DESCENDING);

            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d(TAG, "Error: " + e.getMessage());
                    } else {
                        if (isFirstpageFirstLoad) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                DangerPost dangerPost = doc.getDocument().toObject(DangerPost.class);

                                if (isFirstpageFirstLoad) {
                                    danger_list.add(dangerPost);
                                } else {
                                    danger_list.add(0, dangerPost);
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
}