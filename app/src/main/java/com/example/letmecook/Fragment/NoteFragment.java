package com.example.letmecook.Fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.DanhSachPost;
import com.example.letmecook.R;
import com.example.letmecook.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<DanhSachPost> postList;
    private PostAdapter postAdapter;

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

        loadLatestPosts();

        return view;
    }
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space; // khoảng cách giữa các item (phía dưới)

            // Tuỳ chọn: nếu bạn muốn cách đều cả 4 phía
            // outRect.top = space;
            // outRect.left = space;
            // outRect.right = space;
        }
    }
    private void loadLatestPosts() {
        db.collection("post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        DanhSachPost post = document.toObject(DanhSachPost.class);
                        if (post != null) {
                            postList.add(post);
                            Log.d("TEST_IMAGE_USER", "imageUser = " + document.getString("imageUser"));
                        }
                    }

                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi tải bài post!", e));
    }
}
