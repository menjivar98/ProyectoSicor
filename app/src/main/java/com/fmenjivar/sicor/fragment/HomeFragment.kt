package com.fmenjivar.sicor.fragment


import android.nfc.Tag
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.fmenjivar.sicor.R
import com.fmenjivar.sicor.adapter.PostAdapter
import com.fmenjivar.sicor.models.DangerPost
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

import java.util.ArrayList

import androidx.constraintlayout.widget.Constraints.TAG


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    internal lateinit var post_list_view: RecyclerView
    private var danger_list: MutableList<DangerPost>? = null
    internal lateinit var postAdapter: PostAdapter

    //Firebase
    private var firebaseFirestore: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var lastVisible: DocumentSnapshot? = null
    private var isFirstpageFirstLoad: Boolean? = true
    private var addPost: FloatingActionButton? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        addPost = activity!!.findViewById(R.id.add_post_btn)
        addPost!!.show()

        danger_list = ArrayList()
        post_list_view = view.findViewById(R.id.post_list_view)

        postAdapter = PostAdapter(danger_list)

        post_list_view.adapter = postAdapter
        post_list_view.layoutManager = LinearLayoutManager(activity)

        firebaseAuth = FirebaseAuth.getInstance()


        if (firebaseAuth!!.currentUser != null) {
            firebaseFirestore = FirebaseFirestore.getInstance()

            post_list_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val reachedBottom = !recyclerView.canScrollVertically(-1)

                    if (reachedBottom) {
                        /*
                        String desc = lastVisible.getString("description");

                        Toast.makeText(container.getContext(),"Reached: " + desc,Toast.LENGTH_SHORT ).show();
                        */

                        loadMorePost()

                    }
                }
            })

            val firstQuery = firebaseFirestore!!.collection("Post").orderBy("timestamp", Query.Direction.DESCENDING).limit(3)

            firstQuery.addSnapshotListener(activity!!) { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.d(TAG, "Error: " + e.message)
                } else {

                    if (isFirstpageFirstLoad!!) {
                        lastVisible = queryDocumentSnapshots!!.documents[queryDocumentSnapshots.size() - 1]
                    }

                    for (doc in queryDocumentSnapshots!!.documentChanges) {
                        if (doc.type == DocumentChange.Type.ADDED) {

                            val dangerPost = doc.document.toObject<DangerPost>(DangerPost::class.java!!)

                            if (isFirstpageFirstLoad!!) {
                                danger_list!!.add(dangerPost)
                            } else {
                                danger_list!!.add(0, dangerPost)
                            }

                            postAdapter.notifyDataSetChanged()


                        }
                    }

                    isFirstpageFirstLoad = false
                }
            }

        }

        return view
    }

    fun loadMorePost() {

        val nextQuery = firebaseFirestore!!.collection("Post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible!!)
                .limit(3)

        nextQuery.addSnapshotListener { queryDocumentSnapshots, e ->
            if (!queryDocumentSnapshots!!.isEmpty) {
                lastVisible = queryDocumentSnapshots.documents[queryDocumentSnapshots.size() - 1]
                for (doc in queryDocumentSnapshots.documentChanges) {
                    if (doc.type == DocumentChange.Type.ADDED) {

                        val dangerPost = doc.document.toObject<DangerPost>(DangerPost::class.java!!)
                        danger_list!!.add(dangerPost)

                        postAdapter.notifyDataSetChanged()


                    }
                }
            } else {
                Log.d(TAG, "Error: " + e!!.message)
            }
        }

    }

}// Required empty public constructor
