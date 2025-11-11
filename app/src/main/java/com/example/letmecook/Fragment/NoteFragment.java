package com.example.letmecook.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Activity.CreatPostActivity;
import com.example.letmecook.Model.DanhSachPost;
import com.example.letmecook.R;
import com.example.letmecook.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<DanhSachPost> postList;
    private PostAdapter postAdapter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // RxJava cleanup

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_post, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int spaceInPixels = (int) (10 * getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spaceInPixels));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);


        postAdapter.setOnPostBoxClickListener(() -> {
            Intent intent = new Intent(requireActivity(), CreatPostActivity.class);
            startActivityForResult(intent, 100);
        });


        loadLatestPosts();

        return view;
    }


    private void loadLatestPosts() {
        compositeDisposable.add(
                getPostsFromFirestore()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                posts -> {
                                    postList.clear();
                                    postList.addAll(posts);
                                    postAdapter.notifyDataSetChanged();
                                },
                                error -> {
                                    Log.e("NoteFragment", "Lỗi tải bài viết", error);
                                    Toast.makeText(getContext(), "Lỗi tải bài viết", Toast.LENGTH_SHORT).show();
                                }
                        )
        );
    }


    private Single<List<DanhSachPost>> getPostsFromFirestore() {
        return Single.create(emitter -> {
            db.collection("post")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<DanhSachPost> posts = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot) {
                            DanhSachPost post = doc.toObject(DanhSachPost.class);
                            if (post != null) posts.add(post);
                        }
                        emitter.onSuccess(posts);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            loadLatestPosts();
        }
    }


    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
    }
}
