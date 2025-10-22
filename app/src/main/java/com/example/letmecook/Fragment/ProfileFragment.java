package com.example.letmecook.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Activity.ChangePassWord;
import com.example.letmecook.Activity.LoginActivity;
import com.example.letmecook.Activity.SendRequest;
import com.example.letmecook.Activity.UpCoinActivity;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private TextView Logout, UpCoin,ChanePass,SenRequest;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    TextView tvNameUser, tvProfilename,tvProfilebirth,tvProfileemail,tvLv,tvCoin, tvSetting;
    ImageView imgUser;

    private List<Long> favorites;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvProfilename = view.findViewById(R.id.tvProFileName);
        tvProfilebirth = view.findViewById(R.id.tvProfileBirth);
        tvProfileemail = view.findViewById(R.id.tvProfileEmail);
        Logout = view.findViewById(R.id.logout);
        tvNameUser = view.findViewById(R.id.tvNameUser);
        imgUser = view.findViewById(R.id.imgUser);
        tvLv = view.findViewById(R.id.tvLv);
        tvCoin = view.findViewById(R.id.tvCoinUser);
        SenRequest = view.findViewById(R.id.tvSendRequest);
        UpCoin = view.findViewById(R.id.tvUpCoin);
        tvSetting = view.findViewById(R.id.tvSetting);



        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassWord.class);
                startActivity(intent);
            }
        });

        SenRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendRequest.class);
                startActivity(intent);
            }
        });

        UpCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpCoinActivity.class);
                startActivity(intent);

            }
        });



        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); //

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });




        UserDetail();

        return view;
    }
    private void UserDetail() {


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // Lấy ID user

            db.collection("user").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String avatarUrl = documentSnapshot.getString("avata");
                            String email = documentSnapshot.getString("e-mail");
                            String birth = documentSnapshot.getString("birth");
                            String lv = documentSnapshot.getString("lv");
                            Long coin = documentSnapshot.getLong("coin");
                            int coinInt = coin != null ? coin.intValue() : 0;
                            tvCoin.setText(String.valueOf(coinInt));
                            tvProfileemail.setText(email != null ? email : "Tên không có sẵn");
                            tvProfilename.setText(name != null ? name : "example@email.com");
                            tvProfilebirth.setText(birth != null ? birth : "dd/mm/yy");
                            tvNameUser.setText(name != null ? name : "Chưa xác định");
                            tvLv.setText(lv != null ? lv : "lv chưa có");
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(avatarUrl)
                                        .transform(new RoundedCorners(30))
                                        .into(imgUser);
                            } else {
                                imgUser.setImageResource(R.drawable.placeholder);
                            }


                        } else {

                        }
                    })
                    .addOnFailureListener(e -> Log.e("UserInfo", "Lỗi khi lấy dữ liệu", e));
        } else {
            Log.e("UserInfo", "Người dùng chưa đăng nhập!");
        }
    }
}
